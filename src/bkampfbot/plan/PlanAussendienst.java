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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.exception.FatalError;
import bkampfbot.output.Output;
import bkampfbot.state.Config;
import bkampfbot.state.User;

import json.JSONException;
import json.JSONObject;
import json.JSONTokener;

/**
 * PlanAussendienst benötigt folgende Konfiguration: {"Aussendienst":0} oder
 * {"Aussendienst":{"Stufe":0,"Medizin":50}}
 * 
 * @author georf
 * 
 */

public final class PlanAussendienst extends PlanObject {
	private int difficult;
	private int medicine = -1;

	public PlanAussendienst(JSONObject object) throws FatalError {
		this.setName("Aussendienst");

		try {
			this.difficult = object.getInt("Aussendienst");
		} catch (JSONException e) {

			try {
				JSONObject help = object.getJSONObject("Aussendienst");
				this.difficult = help.getInt("Stufe");

				try {
					this.medicine = help.getInt("Medizin");
				} catch (JSONException e3) {

				}
			} catch (JSONException e2) {
				throw new FatalError("Config error: Aussendienst");
			}
		}

		if (this.difficult > 1) {
			Output.println("Config: Aussendienst is set to 1.", 0);
			this.difficult = 1;
		} else if (this.difficult < 0) {
			Output.println("Config: Aussendienst is set to 0.", 0);
			this.difficult = 0;
		}
	}

	public final void run() throws FatalError {
		Output.printClockLn("-> Aussendienst ("
				+ ((this.difficult == 1) ? "schwer" : "leicht")
				+ ((medicine != -1) ? ", Medizin ab " + medicine + "%" : "")
				+ ")", 1);

		try {
			boolean result = false;
			for (int i = 0; i < 10 && !result; i++) {

				if (i != 0) {
					Output
							.printTabLn(
									"Konnte keinen Dienst finden. Versuche es in 2 min erneut.",
									1);
					Control.sleep(1200, 2);
				}

				result = this.completeService();
			}

			if (!result)
				Output.printTabLn("Abbruch. Kann Dienst nicht finden.", 1);
		} catch (MaxServices e) {
			Output.printTabLn("Maximale Anzahl erreicht.", 1);
		}
	}

	public final static boolean finish() throws FatalError {
		try {
			// HTTP parameters stores header etc.
			HttpParams params = new BasicHttpParams();
			params.setParameter("http.protocol.handle-redirects", false);

			HttpGet httpget = new HttpGet(Config.getHost() + "quests/start");
			httpget.setParams(params);

			HttpResponse response = Control.current.httpclient.execute(httpget);

			// obtain redirect target
			Header locationHeader = response.getFirstHeader("location");
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				resEntity.consumeContent();
			}
			if (locationHeader != null) {

				// difficult
				if (locationHeader.getValue().equalsIgnoreCase(
						(Config.getHost() + "quests/fight").toLowerCase())) {
					Output.printTabLn(
							"Sie haben zuvor einen schweren Dienst gewählt, "
									+ "aber nicht beendet.", 2);
					return PlanAussendienst.difficultFinish();
				} else if (locationHeader.getValue().equalsIgnoreCase(
						(Config.getHost() + "quests/finish").toLowerCase())) {
					Output.printTabLn(
							"Sie haben zuvor einen leichten Dienst gewählt, "
									+ "aber nicht beendet.", 2);
					return PlanAussendienst.easyFinish();
				} else if (locationHeader.getValue().equalsIgnoreCase(
						(Config.getHost() + "quests/doQuest").toLowerCase())) {

					try {
						JSONObject o = Utils.getJSON("quests/questData");
						int serverTs = o.getJSONObject("time").getInt("server");
						int endTs = o.getJSONObject("time").getInt("realend");

						int centiSeconds = endTs - serverTs;
						centiSeconds *= 10;
						centiSeconds += 300;

						Control.sleep(centiSeconds, 1);

						return finish();

					} catch (JSONException e) {
						Output.error(e);
						return false;
					}
				} else {
					Output.printTabLn("Es ging was schief.", 0);
					return false;
				}
			}
		} catch (IOException e) {
			Output.printTabLn("Es ging was schief.", 0);
			return false;
		}

		return true;
	}

	private final boolean completeService() throws MaxServices, FatalError {

		try {
			String s = Utils.getString("quests/start");

			if (s.indexOf("/img/images2/max.jpg") != -1) {
				throw new MaxServices();
			}

			String search = "var quests = ";

			int start = s.indexOf(search);
			if (start == -1) {
				return false;
			}

			start = s.indexOf('\n', start + search.length());
			start = s.indexOf('\n', start + 2);
			int end = s.indexOf("]", start) + 1;
			search = s.substring(start, end);

			search = "{list:[" + search + "}";

			JSONObject o = new JSONObject(new JSONTokener(search));
			JSONObject now = o.getJSONArray("list").getJSONObject(
					this.difficult);

			Output.printTabLn("Nehme: \"" + now.getString("title") + "\"", 2);

			Utils.visit("quests/start/" + now.getInt("questId") + "/0");

			Control.sleep(5);

			o = Utils.getJSON("quests/questData");
			int serverTs = o.getJSONObject("time").getInt("server");
			int endTs = o.getJSONObject("time").getInt("realend");

			int centiSeconds = endTs - serverTs;
			centiSeconds *= 10;
			centiSeconds += 250;

			Control.sleep(centiSeconds, 1);

			if (this.difficult == 0) {
				return easyFinish();
			} else {
				return difficultFinish(medicine);
			}

		} catch (JSONException e) {
			throw new MaxServices();
		}

	}

	private final static boolean difficultFinish() throws FatalError {
		return difficultFinish(-1);
	}

	private final static boolean difficultFinish(int medic) throws FatalError {
		try {
			Utils.visit("quests/finish");

			String pageContent = Utils.getString("quests/fight");
			int first = pageContent
					.indexOf("<div style=\"position:absolute;left:0px;top:0px;width:344;height:675px;border-width:0px;\">");
			first = pageContent.indexOf("var flashvars = ", first);
			first += "var flashvars = ".length();
			int end = pageContent.indexOf("}", first) + 1;
			String flashvars = pageContent.substring(first, end);

			JSONTokener tk = new JSONTokener(flashvars);
			JSONObject character = new JSONObject(tk);
			int fullSec = character.getInt("fullSec");
			String finishUrl = character.getString("finishUrl");

			Utils.visit("http://www.bundeskampf.com" + finishUrl);

			Output.printTab("Ergebnis in " + fullSec + " Sekunden: ", 1);

			Control.sleep(fullSec * 10);

			finishUrl = finishUrl.substring("/quests/results/".length());

			character = Utils.getJSON("quests/resultsData/" + finishUrl);
			if (character.getBoolean("fightWasWon")) {
				Output.println("Kampf gewonnen", 1);
			} else {
				Output.println("Kampf verloren", 1);
			}
			Control.sleep(5);

			Utils.visit("quests/endText");

			if (medic != -1) {
				// Aktualisieren
				Control.current.getCharacter();

				int procent = Math.round((User.getCurrentLivePoints() / User
						.getMaxLivePoints()) * 100);

				// Wenn weniger Prozent Leben als angegen, dann Medizin kaufen.
				if (procent < medic) {
					Utils.buyMedicine();
				}
			}

			Control.current.waitForStatus();

			return true;

		} catch (JSONException e) {
			Output.error(e);
			return false;
		}
	}

	private final static boolean easyFinish() {
		Utils.visit("quests/finish");
		return true;
	}

	private final class MaxServices extends Exception {
		private static final long serialVersionUID = 201102041110L;
	}

	@Override
	public boolean isPreRunningable() {
		return true;
	}
}
