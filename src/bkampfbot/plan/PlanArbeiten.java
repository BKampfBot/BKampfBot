package bkampfbot.plan;

/*
 Copyright (C) 2011  georf@georf.de

 This file is part of BKampfBot.

 BKampfBot is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 2 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.exception.FatalError;
import bkampfbot.output.Output;
import bkampfbot.state.Config;

import json.JSONException;
import json.JSONObject;
import json.JSONTokener;

/**
 * PlanArbeiten benötigt folgende Konfiguration: {"Arbeiten":2} oder
 * {"Arbeiten":{"Stunden":2, "Stopp": true}}
 * 
 * @author georf
 * 
 */
public final class PlanArbeiten extends PlanObject {
	private int hours;
	private boolean stop = false;

	public PlanArbeiten(JSONObject object) throws FatalError {
		this.setName("Arbeiten");

		try {
			this.hours = object.getInt("Arbeiten");
		} catch (JSONException e) {
			try {
				JSONObject o = object.getJSONObject("Arbeiten");
				this.hours = o.getInt("Stunden");
				this.stop = o.getBoolean("Stopp");
			} catch (JSONException en) {
				throw new FatalError(
						"Config error: Arbeiten have to be an integer or a correct object");
			}
		}

		if (this.hours < 1) {
			Output.println("Config: Arbeiten is set to 1 hour.", 0);
			this.hours = 1;
		} else if (this.hours > 10) {
			Output.println("Config: Arbeiten is set to 10 hours.", 0);
			this.hours = 10;
		}
	}

	public final void run() throws FatalError, JSONException {
		Output.printClockLn("-> Arbeiten (" + this.hours + " Stunden)", 1);

		Utils.visit("arbeitsamt/index/gold");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("[Service][txt]", "Hier steht was"));
		nvps.add(new BasicNameValuePair("confirm", "1"));
		nvps.add(new BasicNameValuePair("x", "118"));
		nvps.add(new BasicNameValuePair("y", "26"));

		Utils.getString("services/index/gold/45", nvps);

		Control.sleep(5);

		JSONObject result = Utils.getJSON("services/serviceData");
		String workFee = result.getString("workFee");
		Output.printTabLn("Arbeite für " + workFee + " D-Mark.", 2);

		if (this.stop) {
			Control.safeExit();
		}

		int count = 60 * this.hours + 1;

		if (!Config.getPrevention()) {
			// cut it into parts to safe session
			while (count > 10) {
				// sleep for 10 min
				Control.sleep(6000, 2);
				count -= 10;
				Control.current.getCharacter();
			}
		}

		Control.sleep(600 * count, 2);

		Utils.visit("arbeitsamt/cancel");

	}

	public final static boolean finish() throws FatalError {

		try {
			// HTTP parameters stores header etc.
			HttpParams params = new BasicHttpParams();
			params.setParameter("http.protocol.handle-redirects", false);

			HttpGet httpget = new HttpGet(Config.getHost() + "arbeitsamt/index");
			httpget.setParams(params);

			HttpResponse response = Control.current.httpclient.execute(httpget);

			// obtain redirect target
			Header locationHeader = response.getFirstHeader("location");
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				resEntity.consumeContent();
			}
			if (locationHeader != null) {

				if (locationHeader.getValue().equalsIgnoreCase(
						(Config.getHost() + "arbeitsamt/serve").toLowerCase())) {
					try {
						JSONTokener js = new JSONTokener(Utils
								.getString("services/serviceData"));
						JSONObject result = new JSONObject(js);

						// [truncated]
						// {"currentTime":43,"fullTime":3600,"urlCancel":"\/arbeitsamt\/cancel","urlFinish":"\/arbeitsamt\/finish","workTotalTime":"1","workFee":"112","workFeeType":"gold","workText":"Die
						// Kellnerin im Goldenen Igel kommt nach einem langen
						int seconds = result.getInt("fullTime")
								- result.getInt("currentTime") + 20;
						Output.printTabLn(
								"Letzte Arbeit wurde nicht beendet. Schlafe"
										+ Math.round(seconds / 60)
										+ " Minuten.", 1);

						// cut it into parts to safe session
						while (seconds > 600) {
							// sleep for 10 min
							Control.sleep(6000, 2);
							seconds -= 600;
							Control.current.getCharacter();
						}
						Control.sleep(10 * seconds, 2);

						Utils.visit("arbeitsamt/finish");

					} catch (JSONException e) {
						Output.error(e);
						return false;
					}
				} else if (locationHeader.getValue().equalsIgnoreCase(
						(Config.getHost() + "arbeitsamt/finish").toLowerCase())) {
					Utils.visit("arbeitsamt/finish");
				} else {
					Output.println("Es ging was schief.", 0);
					return false;
				}
			}
		} catch (IOException e) {

			Output.println("Es ging was schief.", 0);
			return false;
		}
		return true;
	}

	@Override
	public boolean isPreRunningable() {
		return true;
	}
}
