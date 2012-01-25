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
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import json.JSONException;
import json.JSONObject;
import json.JSONTokener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.LocationChangedException;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.modes.Gluecksrad;
import bkampfbot.modes.Jagd;
import bkampfbot.modes.Lottery;
import bkampfbot.modes.Pins;
import bkampfbot.modes.Quiz;
import bkampfbot.modes.ScratchTicket;
import bkampfbot.modes.Tagesspiel;
import bkampfbot.modes.TestProxy;
import bkampfbot.modes.Wein;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanAngriff;
import bkampfbot.plan.PlanArbeiten;
import bkampfbot.plan.PlanAussendienst;
import bkampfbot.plan.PlanBoeseBeute;
import bkampfbot.plan.PlanBoeseKrieg;
import bkampfbot.plan.PlanBoeseRespekt;
import bkampfbot.state.Config;
import bkampfbot.state.User;

public final class Instance {

	public enum Daily {
		quiz, scratchTicket, glueck, wein, spiel, jagd
	}

	public enum Modus {
		normal, daily, help, lottery, pins, testproxy
	}

	// Configuration from file
	public int additionalService = 0;
	public boolean lookAhead = false;

	// Modes
	private Modus modus = Modus.normal;
	private Daily[] daily;

	// Instance
	public DefaultHttpClient httpclient;

	/**
	 * Parst die Parameter und liest die Konfigurationsdatei aus.
	 * 
	 * @param args
	 * @throws FatalError
	 * @throws IOException
	 * @throws RestartLater
	 */
	public Instance(String[] args) throws FatalError, IOException {

		// configure plans
		PlanBoeseBeute.initiate();
		PlanBoeseKrieg.initiate();
		PlanBoeseRespekt.initiate();
		PlanAngriff.initiate();

		// reset config
		new Config();

		// reset user
		new User();

		this.parseArguments(args);

		if (this.modus.equals(Modus.help)) {
			Output.help();
		}

		// read config file
		try {
			File f = new File(Config.getConfigFile());
			FileInputStream fstream = new FileInputStream(f.getAbsoluteFile());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine, configString = "";

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {

				// Kommentare werden ausgelassen
				if (strLine.length() > 0 && strLine.substring(0, 1).equals("#")) {
					continue;
				}
				configString += strLine + "\n";
			}

			// Close the input stream
			in.close();

			try {
				JSONObject config = new JSONObject(
						new JSONTokener(configString));
				Config.parseJsonConfig(config);
			} catch (JSONException e) {
				throw new FatalError("Die Struktur der Konfigurationsdatei "
						+ "stimmt nicht. Versuche den Inhalt der "
						+ "Datei mit einem externen Werkzeug zu "
						+ "reparieren. Dafür gibt es Webseiten, "
						+ "die JSON-Objekte validieren können."
						+ "\n\nAls Hinweis hier noch die Fehlerausgabe:\n"
						+ e.getMessage() + "\n\nEingabe war:\n"
						+ e.getInputString());
			}
		} catch (FileNotFoundException e) {
			throw new FatalError(
					"Die Konfigurationsdatei konnte nicht gefunden/geöffnet werden.\n Datei: "
							+ Config.getConfigFile() + "\n Fehler: "
							+ e.getMessage());
		}

		if (Config.getUserName() == null || Config.getUserPassword() == null
				|| Config.getHost() == null) {
			throw new FatalError(
					"Die Konfigurationsdatei ist nicht vollständig. "
							+ "Es wird mindestens der Benutzername, das "
							+ "Passwort und der Hostname benötigt.");
		}

	}

	private final boolean finishOldWork() throws FatalError {
		Output.printClockLn("Versuche alte Aufgaben zu beenden.", 2);
		boolean returnValue = false;

		switch (User.getStatus()) {
		case work:
			returnValue = PlanArbeiten.finish();
			break;

		case service:
			returnValue = PlanAussendienst.finish();
			break;

		case training:
			Output.printTabLn("Versuche das Training zu beenden.", 1);
			Utils.visit("training");
			returnValue = true;
			break;

		case nothing:
		default:
			returnValue = true;
			break;
		}

		this.getCharacter();
		return returnValue;
	}

	public final String getCharacter() throws FatalError {
		return this.getCharacter(false);
	}

	/**
	 * Sammelt Informationen über Benutzer
	 * 
	 * @param login
	 *            gibt an, ob es der Aufruf nach dem Login ist
	 */
	private final String getCharacter(boolean login) throws FatalError {
		HttpGet httpget = new HttpGet(Config.getHost() + "characters/index");
		httpget
				.addHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpget.addHeader("Accept-Language",
				"de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
		// httpget.addHeader("Accept-Encoding","deflate");
		httpget.addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		httpget.addHeader("Keep-Alive", "300");

		if (login) {
			httpget.addHeader("Referer", Config.getHost() + "signups/login");
		}

		String s = "";

		try {
			// Create a response handler
			HttpResponse response = this.httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				s = EntityUtils.toString(entity);

				this.testStatus(s);

				int navi2 = s.indexOf("/img/flash/navi_or2.swf");
				navi2 = s.indexOf("flashvars", navi2 + 1);
				navi2 = s.indexOf("flashvars", navi2 + 1);
				int lineFront = s.indexOf('{', navi2);
				int lineEnd = s.indexOf(';', lineFront + 1);
				String s2 = s.substring(lineFront + 1, lineEnd + 1);
				s2 = "{" + s2;

				JSONTokener tk = new JSONTokener(s2);
				JSONObject character = new JSONObject(tk);
				User.setLevel(Integer.parseInt(character.getString("lvl")));
				User.setMaxLivePoints(Integer.parseInt(character
						.getString("max_lp")));
				User.setCurrentLivePoints(Integer.parseInt(character
						.getString("lp")));
				User.setGold(Integer.parseInt(character.getString("water")));

				// try to find race
				s2 = "<b>Bundesland:</b> </span><br/><span style=\"color:#000000; font-size:12px;\">";

				lineFront = s.indexOf(s2);
				lineEnd = s.indexOf('<', lineFront + 1 + s2.length());

				User.setRace(s.substring(lineFront + s2.length() + 1,
						lineEnd - 1));
				// Output.user(user);
			}
		} catch (JSONException e) {
			if (s.contains("form action=\"/signups/login\" method=\"post\"")) {
				throw new FatalError("Login fehlgeschlagen. "
						+ "Vermutlich sind die Benutzerdaten nicht korrekt.");
			}
			String message = "Get an error at initiation\n";
			if (login) {
				message += "Reason 1: Login failed\n"
						+ "Reason 2: Something on server side changed.\n";
			} else {
				message += "Possible reason: Something on server side changed.\n";
			}

			message += "\n"
					+ "If you want to report a bug, please post this:\n\n"
					+ e.getMessage() + "\n";

			StackTraceElement[] trace = e.getStackTrace();
			for (StackTraceElement elem : trace) {
				message += elem.toString() + "\n";
			}
			throw new FatalError(message + "\nResponse was:\n" + s);
		} catch (IOException e) {
			Output.error(e);
			throw new FatalError("Es gab einen Verbindungsfehler.");
		}

		return s;
	}

	/**
	 * Führt den Login durch
	 */
	private final void login() throws FatalError {

		try {
			Output.printClockLn("Login (" + Config.getUserName() + ")", 1);

			// create post data
			HttpPost httppost = new HttpPost(Config.getHost()
					+ "signups/login/");

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("data[Signup][name]", Config
					.getUserName()));
			nvps.add(new BasicNameValuePair("data[Signup][pass]", Config
					.getUserPassword()));
			nvps.add(new BasicNameValuePair("Einloggen.x", "67"));
			nvps.add(new BasicNameValuePair("Einloggen.y", "18"));

			httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httppost
					.addHeader("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httppost.addHeader("Accept-Language",
					"de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
			httppost.addHeader("Accept-Encoding", "gzip,deflate");
			httppost.addHeader("Accept-Charset",
					"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			httppost.addHeader("Keep-Alive", "300");
			httppost.addHeader("Referer", Config.getHost() + "signups/login");

			// send post
			HttpResponse response = this.httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				entity.consumeContent();
			}

			this.getCharacter(true);

			// Ist User im Verein?
			try {
				Utils.getString("verein/buendnisse", "verein/index");
				User.setGuildMember(true);
			} catch (LocationChangedException e) {
				User.setGuildMember(false);
			}

		} catch (IOException e) {
			Output.println(e.getMessage(), 0);
			throw new FatalError(e.getMessage());
		}
	}

	/**
	 * Führt das Logout durch
	 */
	public final void logout() {
		Output.printClockLn("Logout", 1);
		Utils.visit("signups/logout");
	}

	/**
	 * Prüft übergebene Argumente
	 * 
	 * @param args
	 */
	private final void parseArguments(String[] args) {
		this.daily = new Daily[args.length];
		int dailyCount = 0;

		for (String arg : args) {
			if (arg.startsWith("quiz")
					&& (this.modus.equals(Modus.normal) || this.modus
							.equals(Modus.daily))
					&& (arg.equals("quiz") || arg.matches("^quiz=[1-9]$"))) {

				if (arg.matches("^quiz=[1-9]$")) {
					Quiz.wrongAnswerCount = Integer.parseInt(arg.replaceAll(
							"[^1-9]", ""));
				}

				this.modus = Modus.daily;
				this.daily[dailyCount] = Daily.quiz;
				dailyCount++;

				continue;
			}

			if (arg.equals("los")
					&& (this.modus.equals(Modus.normal) || this.modus
							.equals(Modus.daily))) {
				this.modus = Modus.daily;
				this.daily[dailyCount] = Daily.scratchTicket;
				dailyCount++;

				continue;
			}

			if (arg.startsWith("lotto") && this.modus.equals(Modus.normal)) {

				if (arg.equals("lotto")) {
					this.modus = Modus.lottery;
					continue;
				} else if (arg
						.matches("^lotto=(([1-9]|([1-4][0-9])),){4}([1-9]|([1-4][0-9]))$")) {
					this.modus = Modus.lottery;
					String[] numbers = arg.replaceAll("[^0-9,]+", "")
							.split(",");

					if (numbers.length != 5) {
						Output.println("Lottozahlen sind ungültig",
								Output.ERROR);
					}

					for (int i = 0; i < 5; i++) {
						Lottery.numbers[i] = Integer.parseInt(numbers[i]);
					}

					Lottery.random = false;

					continue;
				}

			}

			if (arg.length() > 5
					&& arg.substring(0, 5).equalsIgnoreCase("pins=")
					&& this.modus.equals(Modus.normal)) {
				Pins.in = arg.substring(5);
				this.modus = Modus.pins;

				continue;
			}

			if (arg.equals("glueck")
					&& (this.modus.equals(Modus.normal) || this.modus
							.equals(Modus.daily))) {
				this.modus = Modus.daily;
				this.daily[dailyCount] = Daily.glueck;
				dailyCount++;

				continue;
			}

			if (arg.equals("wein")
					&& (this.modus.equals(Modus.normal) || this.modus
							.equals(Modus.daily))) {
				this.modus = Modus.daily;
				this.daily[dailyCount] = Daily.wein;
				dailyCount++;

				continue;
			}

			if (arg.equals("spiel")
					&& (this.modus.equals(Modus.normal) || this.modus
							.equals(Modus.daily))) {
				this.modus = Modus.daily;
				this.daily[dailyCount] = Daily.spiel;
				dailyCount++;

				continue;
			}

			if (arg.equals("jagd")
					&& (this.modus.equals(Modus.normal) || this.modus
							.equals(Modus.daily))) {
				this.modus = Modus.daily;
				this.daily[dailyCount] = Daily.jagd;
				dailyCount++;

				continue;
			}

			if (arg.length() > 9
					&& arg.substring(0, 9).equalsIgnoreCase("--config=")) {
				Config.setConfigFile(arg.substring(9));

				continue;
			}

			if (arg.equals("--help") || args.equals("-h")) {
				this.modus = Modus.help;

				continue;
			}

			if (arg.equals("testproxy") && this.modus.equals(Modus.normal)) {
				this.modus = Modus.testproxy;

				continue;
			}

			// Argument passt nicht
			Output.println("Argument \"" + arg + "\" nicht gültig",
					Output.ERROR);
		}
	}

	/**
	 * Führt die aktuelle Instans aus
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws FatalError
	 * @throws JSONException
	 * @throws RestartLater
	 * @throws NoSuchAlgorithmException
	 */
	public final void run() throws FatalError, RestartLater {

		// initialization HTTP client

		this.httpclient = new DefaultHttpClient();

		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUseExpectContinue(params, false);

		HttpProtocolParams.setUserAgent(params, Config.getUserAgent());

		this.httpclient.setParams(params);

		if (Config.getProxyHost() != null && Config.getProxyPort() != 0) {
			if (Config.getProxyUsername() != null
					&& Config.getProxyPassword() != null) {
				httpclient.getCredentialsProvider()
						.setCredentials(
								new AuthScope(Config.getProxyHost(), Config
										.getProxyPort()),
								new UsernamePasswordCredentials(Config
										.getProxyUsername(), Config
										.getProxyPassword()));
			}

			HttpHost proxy = new HttpHost(Config.getProxyHost(), Config
					.getProxyPort());
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);

		}

		this.httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);

		switch (this.modus) {
		// default: do nothing
		default:
			break;

		// call help
		case help:
			Output.help();
			System.exit(0);
			break;

		// call proxy test
		case testproxy:
			new TestProxy();
			System.exit(0);
			break;

		}
		// login
		this.login();

		switch (this.modus) {

		// for things without login
		case help:
		case testproxy:
			break;

		case daily:

			for (Daily d : this.daily) {

				if (d == null)
					break;

				switch (d) {
				// call "Rubbellos"
				case scratchTicket:
					new ScratchTicket();
					break;

				// call "Tagesquiz"
				case quiz:
					new Quiz();
					break;

				// call "Lotto"
				case glueck:
					new Gluecksrad();
					break;

				// call "Weinfässer"
				case wein:
					new Wein();
					break;

				// call "Tagesspiel"
				case spiel:
					new Tagesspiel();
					break;

				// call "Wörterjagd"
				case jagd:
					new Jagd();
					break;

				}
			}
			Control.safeExit();
			break;

		// call "Lotto"
		case lottery:
			new Lottery();
			Control.safeExit();
			break;

		// call "Pins"
		case pins:
			new Pins();
			Control.safeExit();
			break;

		// call "Plan"
		default:
		case normal:

			if (Config.getPlan0() == null && Config.getPlan1() == null) {
				Output.printTabLn("Beide Pläne sind leer. "
						+ "Bitte überprüfen Sie die Konfiguration.", 0);
				System.exit(1);
			}

			Calendar lastCall = null;
			while (true) {
				Calendar now = new GregorianCalendar();
				now.setTime(Config.getDate());
				now.add(Calendar.MINUTE, -2);
				if (lastCall != null && lastCall.after(now)) {
					Output
							.println(
									"Der Bot war bei der Abarbeitung der Pläne zu schnell. "
											+ "Vermutlich trat ein Fehler auf. Damit wir nicht auffallen, "
											+ "warten wir 5 Minuten.", 1);

					Control.sleep(3000);

				}
				lastCall = new GregorianCalendar();
				this.runPlans();

				Control.sleep(5);

				this.getCharacter();

				Control.sleep(5);

			}
		}
	}

	/**
	 * Durchläuft beide Pläne genau ein mal
	 * 
	 * @throws FatalError
	 * @throws RestartLater
	 *             wird von Plänen geworfen
	 */
	private final void runPlans() throws FatalError, RestartLater {

		// first check for old not finished task
		if (!User.getStatus().equals(User.Status.nothing)) {
			for (int i = 0; i < 3 && !this.finishOldWork(); i++) {
				Control.sleep(3600);
			}

			if (!User.getStatus().equals(User.Status.nothing)) {
				throw new FatalError("Konnte die alte Aufgabe nicht beenden.");
			}
		}

		// Ab Version 1.0.beta11 muss plan0 nicht gesetzt sein
		if (Config.getPlan0() != null) {
			Config.getPlan0().run();
		}

		// Ab Version 1.0.beta11 muss plan1 nicht gesetzt sein
		if (Config.getPlan1() != null) {
			Config.getPlan1().run();
		}
	}

	/**
	 * Überprüft das Ergebniss der Profilseite auf Hinweise zur aktuellen
	 * Tätigkeit.
	 * 
	 * @param HTML
	 */
	private final void testStatus(String s) {
		if (s.indexOf("/Bilderx/arbeiten.jpg") != -1) {
			User.setStatus(User.Status.work);
			Output.printTabLn("Du bist gerade am Arbeiten", 2);
		} else if (s.indexOf("/Bilderx/train.jpg") != -1) {
			User.setStatus(User.Status.training);
			Output.printTabLn("Du bist gerade am Trainieren", 2);
		} else if (s.indexOf("/Bilderx/dienst.jpg") != -1) {
			User.setStatus(User.Status.service);
			Output.printTabLn("Du bist im Außendienst", 2);
		} else {
			User.setStatus(User.Status.nothing);
		}
	}

	/**
	 * Wartet bis das Leben aufgefüllt ist.
	 * 
	 * @throws FatalError
	 * @throws IOException
	 * @throws ClientProtocolException
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws FatalError
	 */
	public final void waitForStatus() throws FatalError {

		getCharacter();

		while (User.getCurrentLivePoints() != User.getMaxLivePoints()) {

			Output.printTabLn("Warte bis Leben aufgefüllt ist.", 2);

			Control.sleep(300, 2);

			getCharacter();
		}
	}
}
