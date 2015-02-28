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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.LocationChangedException;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.state.Config;
import bkampfbot.utils.ErrorLog;

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

	public static final JSONObject getJSON(String url) throws JSONException {
		String page = repaireJSONString(getString(url));
		return new JSONObject(new JSONTokener(page));
	}
	
	private static final String repaireJSONString(String page) {
		return page.replaceAll("^[^{]*\\{", "{");
	}

	public static final JSONObject getJSON(String url, List<NameValuePair> nvpl)
			throws JSONException {
		String page = repaireJSONString(getString(url, nvpl));
		return new JSONObject(new JSONTokener(page));
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
			if (Config.getDebug())
				Output.println("getString: " + Config.getHost() + url, 2);

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

				// for debugging
				Control.current.lastResponse = ret;

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
	 * 
	 * <pre>
	 * List&lt;NameValuePair&gt; nvps = new ArrayList&lt;NameValuePair&gt;();
	 * nvps.add(new BasicNameValuePair(&quot;name&quot;, &quot;value&quot;));
	 * </pre>
	 * 
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

				// for debugging
				Control.current.lastResponse = ret;

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
		if (Config.getDebug())
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

		if (Config.getDebug()) {
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
		if (badPosition(pos))
			return false;

		page = page.substring(pos);

		pos = page.indexOf("</div>");
		if (badPosition(pos))
			return false;

		page = page.substring(0, pos);

		pos = page.indexOf("<br />");
		if (badPosition(pos))
			return false;

		page = page.substring(pos + 6);

		pos = page.indexOf("<");
		if (badPosition(pos))
			return false;

		page = page.substring(0, pos);

		page = page.replaceAll("[^0-9/]+", "");

		pos = page.indexOf('/');
		if (badPosition(pos))
			return false;

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

	public static final boolean badPosition(int pos) {
		if (pos == -1) {
			new ErrorLog("Falsche Position\n\n" + Control.current.lastResponse);
			return true;
		}
		return false;
	}

	public static Map<String, String> getUrlParameters(String url) {
		Map<String, String> params = new HashMap<String, String>();

		String[] urlParts = url.split("\\?");
		if (urlParts.length > 1) {
			url = urlParts[1];
		}

		for (String param : url.split("&")) {

			try {
				String pair[] = param.split("=");

				String key = URLDecoder.decode(pair[0], "UTF-8");

				String value = "";
				if (pair.length > 1) {
					value = URLDecoder.decode(pair[1], "UTF-8");
				}

				params.put(key, value);
			} catch (UnsupportedEncodingException e) {
			}
		}

		return params;
	}
}
