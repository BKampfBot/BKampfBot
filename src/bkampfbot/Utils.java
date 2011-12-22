package bkampfbot;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import json.JSONTokener;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import bkampfbot.exception.BadOpponent;
import bkampfbot.exception.FatalError;
import bkampfbot.exception.LocationChangedException;
import bkampfbot.exception.RestartLater;
import bkampfbot.output.FightResult;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanObject;
import bkampfbot.state.Config;
import bkampfbot.state.User;

public class Utils {

	/**
	 * Kauft Direkt Fit
	 */
	public static final void buyMedicine() {
		String page = getString("quests/wait");

		// <b>Kostet dich <span>43</span>
		int offset = page.indexOf("h <span>");
		if (offset == -1) {

			// Für <span>43</span>
			offset = page.indexOf("r <span>");

			if (offset == -1)
				return;
		}

		page = page.substring(offset);
		offset = page.indexOf("</span>");
		if (offset == -1)
			return;

		page = page.substring(0, offset);
		page = page.replaceAll("[^0-9]+", "");

		Output.printClockLn("Kaufe Direkt Fit für " + page + " D-Mark", 1);
		visit("quests/wait/buy");
	}

	/**
	 * 
	 * @param attack
	 * @param name
	 * @param method
	 * @param medicine
	 * @param current
	 * @param buyCrystal
	 * @return Gewonnenes Geld
	 * @throws BadOpponent
	 * @throws FatalError
	 * @throws RestartLater
	 */
	public static final int fight(String attack, String name, String method,
			int medicine, PlanObject current, boolean buyCrystal) throws BadOpponent, FatalError,
			RestartLater {

		Utils.visit("fights/fight");
		Utils.visit(attack.substring(1));

		Output.printTab("Kampf mit " + name + " - ", 1);

		Control.sleep(5);

		int returnValue = -1;
		try {
			JSONObject fightData = Utils.getJSON("fights/fightData");
			JSONObject fight = Utils.getJSON(fightData.getString("url")
					.replace("/results/", "/getResults/").substring(1));

			new FightResult(fight, fightData, method);

			JSONObject res = fight.getJSONObject("results");
			JSONObject p1 = res.getJSONObject("p1");

			if (res.getBoolean("fightWasWon")) {
				Output.print("gewonnen", 1);

				returnValue = p1.getInt("gold");
			} else {
				Output.print("verloren", 1);
			}

			Output.println(" (" + p1.getInt("gold") + ", hp:" + p1.getInt("hp")
					+ ")", 1);

			User.setLevel(Integer.parseInt(fight.getString("mylevel")));
			User.setCurrentLivePoints(Integer.parseInt(p1.getString("lp")));
			User.setMaxLivePoints(Integer.parseInt(p1.getString("maxLp")));

			int deci = -1;
			
			if (buyCrystal) {
				Output.printTab("Gebe Zwerg: ", Output.INFO);
				try {
					getString("fights/waitFight/buy", "fights/start");
					// not bought
					Output.println("Fehler", Output.INFO);
				} catch (LocationChangedException e) {
					// bought
					Output.println("Erfolgreich", Output.INFO);
					
					// no medicine need
					medicine = -1;
					
					// no time needed
					deci = 10;
				}
			}

			if (medicine != -1) {
				int procent = Math.round((User.getCurrentLivePoints() / User
						.getMaxLivePoints()) * 100);

				// Wenn weniger Prozent Leben als angegen, dann Medizin kaufen.
				if (procent < medicine) {
					Utils.buyMedicine();

					// Ende anders berechnen

					String page = Utils.getString("fights/waitFight");
					int offset = page
							.indexOf("left:511px; top:347px; width:166px; height:173px");
					page = page.substring(offset);

					offset = page.indexOf("toTime");
					page = page.substring(offset);

					offset = page.indexOf("\n");
					page = page.substring(0, offset);

					page = page.replaceAll("[^0-9]+", "");

					deci = Integer.parseInt(page) * 10;

				}
			}

			if (deci == -1) {
				JSONObject time = fight.getJSONObject("aTime");
				GregorianCalendar c = new GregorianCalendar(Integer
						.parseInt(time.getString("toYear")), Integer
						.parseInt(time.getString("toMonth")) - 1, Integer
						.parseInt(time.getString("toDay")), Integer
						.parseInt(time.getString("toHour")), Integer
						.parseInt(time.getString("toMinute")), Integer
						.parseInt(time.getString("toSecond")));

				GregorianCalendar now = new GregorianCalendar();
				now.setTime(Config.getDate());
				deci = (int) ((c.getTimeInMillis() - now.getTimeInMillis()) / 100);
			}
			// Add 20 seconds
			deci += 200;

			GregorianCalendar beforeLookAhead = new GregorianCalendar();

			if (current != null) {
				// Lookahead
				deci = current.runNextPlan(deci);
			}

			// restliche Zeit abziehen
			deci = deci
					- Math
							.round((new GregorianCalendar().getTimeInMillis() - beforeLookAhead
									.getTimeInMillis()) / 100);

			Control.sleep(deci, 1);

			Utils.visit("fights/start");

			return returnValue;

		} catch (JSONException e) {

			Output.println("nichts", 1);
			throw new BadOpponent(attack, name);
		}
	}

	public final static boolean fightAvailable(int retry) {

		for (int i = retry; i > 0; i--) {
			if (i != retry) {
				Control.sleep(600);
				Output.println(" - versuche erneut", 1);
			}

			try {
				// HTTP parameters stores header etc.
				HttpParams params = new BasicHttpParams();
				params.setParameter("http.protocol.handle-redirects", false);

				HttpGet httpget = new HttpGet(Config.getHost() + "fights/start");
				httpget.setParams(params);

				HttpResponse response = Control.current.httpclient
						.execute(httpget);

				// obtain redirect target
				Header locationHeader = response.getFirstHeader("location");
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					resEntity.consumeContent();
				}
				if (locationHeader == null) {
					return true;
				}

			} catch (IOException e) {
			}
			Output.printTab("Kampf nicht verfügbar", 1);

		}
		Output.println(" - Abbruch", 1);
		return false;
	}

	public final static String findAttackById(String id) {
		String page = Utils.getString("characters/profile/" + id);

		int pos = page.indexOf("href=\"/fights/start/");
		if (pos == -1)
			return null;
		page = page.substring(pos);
		page = page.substring(0, page.indexOf("<img"));

		Pattern p = Pattern.compile("/fights/start/([0-9]+)\"",
				Pattern.MULTILINE);
		Matcher m = p.matcher(page);
		if (m.find())
			return "/fights/start/" + m.group(1);

		return null;
	}

	/**
	 * Sucht den Benutzer in der WildeKeilereiListe und gibt die Angriffsurl
	 * zurück
	 * 
	 * @param Name
	 *            des Gegners
	 * @return Url zum Kämpfen
	 */
	public final static String findAttackByName(String name) {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("playerName", name));
		try {
			JSONObject ob = Utils.getJSON("quests/start", nvps);

			JSONArray arr = ob.getJSONArray("list");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject player = arr.getJSONObject(i);
				if (player.getString("name").equalsIgnoreCase(name)) {
					return player.getString("attack");
				}
			}
			Output.printTabLn("Player " + name + " not found", 1);
			return null;
		} catch (JSONException e) {
			Output.printTabLn("Player " + name + " not found", 1);
			return null;
		}

	}

	public static final JSONObject getJSON(String url) throws JSONException {
		return new JSONObject(new JSONTokener(getString(url)));
	}

	public static final JSONObject getJSON(String url, List<NameValuePair> nvpl)
			throws JSONException {
		return new JSONObject(new JSONTokener(getString(url, nvpl)));
	}

	/**
	 * Lädt eine URL und gibt die Seite als String zurück
	 * 
	 * @param url
	 *            without host
	 * @return HTML
	 */
	public static final String getString(String url) {
		try {
			return getString(url, "");
		} catch (LocationChangedException e) {
			return "";
		}
	}

	public static final String getString(String url, String location)
			throws LocationChangedException {

		try {
			if (Control.debug)
				Output.println("getString: " + url, 2);

			HttpGet httpget = new HttpGet(Config.getHost() + url);
			if (location != "") {
				// HTTP parameters stores header etc.
				HttpParams params = new BasicHttpParams();
				params.setParameter("http.protocol.handle-redirects", false);
				httpget.setParams(params);
			}

			// Create a response handler
			HttpResponse response = Control.current.httpclient.execute(httpget);

			if (location != "") {
				Header locationHeader = response.getFirstHeader("location");

				if (locationHeader != null) {

					if (locationHeader.getValue().equalsIgnoreCase(
							Config.getHost() + location)) {
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							entity.consumeContent();
						}
						throw new LocationChangedException();
					}
				}
			}

			Header date = response.getFirstHeader("date");
			if (date != null) {
				try {
					Config.setLastDate(DateUtils.parseDate(date.getValue()));
				} catch (DateParseException e) {

				}
			}

			HttpEntity entity = response.getEntity();

			Control.sleep(1);

			if (entity != null) {
				String ret = EntityUtils.toString(entity);
				if (entity != null) {
					entity.consumeContent();
				}
				return ret;
			} else {
				return "";
			}
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * Example: 
	 * <pre>
	 *  List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	 *  nvps.add(new BasicNameValuePair("name", "value"));
	 * </pre>
	 * @param url
	 * @param nvpl
	 * @return
	 */
	public static final String getString(String url, List<NameValuePair> nvpl) {

		try {
			// Post senden
			HttpPost http = new HttpPost(Config.getHost() + url);

			http.setEntity(new UrlEncodedFormEntity(nvpl, HTTP.UTF_8));

			// Create a response handler
			HttpResponse response = Control.current.httpclient.execute(http);
			HttpEntity entity = response.getEntity();

			Header date = response.getFirstHeader("date");
			if (date != null) {
				try {
					Config.setLastDate(DateUtils.parseDate(date.getValue()));
				} catch (DateParseException e) {

				}
			}

			if (entity != null) {
				String ret = EntityUtils.toString(entity);
				if (entity != null) {
					entity.consumeContent();
				}
				return ret;
			} else {
				return "";
			}
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * Lädt eine URL und verwirft das Ergebnis
	 * 
	 * @param url
	 */
	public static final void visit(String url) {
		if (Control.debug)
			Output.printTabLn("besuche: " + url, 2);

		getString(url);

		Control.sleep(5);
	}

	/**
	 * Testet, ob der Außendienst verfügbar ist
	 * 
	 * @return true, wenn verfügbar, sonst false
	 * @throws RestartLater
	 * @throws FatalError
	 */
	public static final boolean serviceAvalible() throws RestartLater,
			FatalError {

		if (Control.debug) {
			Output.println("Teste Außendienst möglich", 2);
		}

		String page;
		do {
			try {
				page = Utils.getString("quests/start", "quests/wait");
				break;
			} catch (LocationChangedException e) {
				Control.current.waitForStatus();
			}
		} while (true);
		int pos = page.indexOf("class=\"questanzahl\"");
		if (pos == -1) {
			throw new RestartLater();
		}
		page = page.substring(pos);
		page = page.substring(0, page.indexOf("</div>"));

		page = page.substring(page.indexOf("<br />") + 6);
		page = page.substring(0, page.indexOf(" "));

		page = page.replaceAll("[^0-9/]+", "");

		pos = page.indexOf('/');
		int available = Integer.parseInt(page.substring(0, pos));
		int maximum = Integer.parseInt(page.substring(pos + 1));

		return (available < maximum + Control.current.additionalService);
	}

	public static final String getLine() {

		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		try {
			return in.readLine();
		} catch (IOException e) {
			return "";
		}
	}

	public static int raceToId(String race) {
		HashMap<String, Integer> races = new HashMap<String, Integer>();
		races.put("baden-württemberg", 1);
		races.put("bayern", 2);
		races.put("berlin", 3);
		races.put("brandenburg", 4);
		races.put("bremen", 5);
		races.put("hamburg", 6);
		races.put("hessen", 7);
		races.put("mecklenburg-vorpommern", 8);
		races.put("niedersachsen", 9);
		races.put("nordrhein-westfalen", 10);
		races.put("rheinland-pfalz", 11);
		races.put("saarland", 12);
		races.put("sachsen", 13);
		races.put("sachsen-anhalt", 14);
		races.put("schleswig-holstein", 15);
		races.put("thüringen", 16);

		if (races.get(race.toLowerCase()) == null) {
			return 0;
		} else {
			return races.get(race.toLowerCase());
		}
	}
}
