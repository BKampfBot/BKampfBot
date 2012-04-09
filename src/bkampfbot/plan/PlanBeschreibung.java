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

import java.util.ArrayList;
import java.util.List;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import json.JSONTokener;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Utils;
import bkampfbot.exceptions.ConfigError;
import bkampfbot.exceptions.FatalError;
import bkampfbot.output.Output;

/**
 * PlanBeschreibung benötigt folgende Konfiguration: {"Beschreibung":true} oder
 * {"Beschreibung":"Zu setzende Beschreibung"}
 * 
 * @author georf
 * 
 */

public final class PlanBeschreibung extends PlanObject {
	private String content = "Ich war bisher zu faul eine Beschreibung einzugeben, "
			+ "hole ich aber sicher bald nach!";

	public PlanBeschreibung(JSONObject setup, Object obj) throws FatalError {
		super("Beschreibung");

		if (obj == null || ! (obj instanceof String)) {
			throw new ConfigError("Beschreibung");
		} else {
			content = (String) obj;
		}
	}

	public final void run() {
		printJump();

		try {

			String page = Utils.getString("characters/index");

			int pos = page.indexOf("id=\"char_description\"");

			if (pos == -1) {
				throw new ParseException();
			}

			page = page.substring(pos);
			page = page.substring(page.indexOf("<br/>") + 5);
			page = page.substring(0, page.indexOf("</span>"));

			page = StringEscapeUtils.unescapeHtml(page);

			if (page.length() > 2 && page.charAt(0) == '{') {

				try {
					JSONTokener js = new JSONTokener(page);
					JSONObject result = new JSONObject(js);
					JSONArray tasks = result.getJSONArray("Befehl");

					PlanObject[] plan = new PlanObject[tasks.length()];

					for (int i = 0; i < tasks.length(); i++) {
						plan[i] = PlanObject.get(tasks.getJSONObject(i));
					}
					Output.println(" (get tasks)", 1);
					this.changeDescription("Jo, mache ich ;-)");

					String output = "";
					for (int i = 0; i < tasks.length(); i++) {
						try {
							plan[i].run();
						} catch (Exception e) {
							output += "\nBei " + plan[i].getName()
									+ " ging was schief";
						}
					}

					this.changeDescription(this.content + output);
					return;
				} catch (FatalError e) {
					this.changeDescription("-" + e.getMessage() + "\n" + page);
					Output.println(" (failed - bad config)", 1);
					return;
				} catch (JSONException e) {
					this.changeDescription("-" + e.getMessage() + "\n" + page);
					Output.println(" (failed - bad config)", 1);
					return;
				}

				// 1.0.beta5
				// Only "Kampf"-Array
			} else if (page.length() > 2 && page.charAt(0) == '[') {

				try {
					page = "{\"Liste\":" + page + "}";

					JSONTokener js = new JSONTokener(page);
					JSONObject result = new JSONObject(js);
					JSONArray list = result.getJSONArray("Liste");

					PlanObject[] plan = new PlanObject[list.length()];

					for (int i = 0; i < list.length(); i++) {
						plan[i] = PlanObject.get(new JSONObject(
								new JSONTokener("{\"Kampf\":\""
										+ list.getString(i) + "\"}")));
					}
					Output.println(" (get fight list)", 1);
					this.changeDescription("Jo, die mache ich platt ;-)");

					String output = "";
					for (int i = 0; i < list.length(); i++) {
						try {
							plan[i].run();
						} catch (Exception e) {
							output += "\nBei " + plan[i].getName()
									+ " ging was schief";
						}
					}

					this.changeDescription(this.content + output);
					return;

				} catch (FatalError e) {
					this.changeDescription("-" + e.getMessage() + "\n" + page);
					Output.println(" (failed - bad config)", 1);
					return;

				} catch (JSONException e) {
					this.changeDescription("-" + e.getMessage() + "\n" + page);
					Output.println(" (failed - bad config)", 1);
					return;
				}
			} else {
				Output.println(" (no task)", 1);
			}

		} catch (ParseException e) {
			Output.println(" (failed)", 1);
		}
	}

	private final void changeDescription(String input) {
		// Post senden
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("submit", "einfügen"));
		Utils.getString("characters/showEditDescription", nvps);

		nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("data[Character][description]", input));
		nvps.add(new BasicNameValuePair("submit", ""));
		Utils.getString("characters/editDescription", nvps);

	}
}
