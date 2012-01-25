package bkampfbot.output;

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

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import json.JSONException;
import json.JSONObject;

import bkampfbot.Control;
import bkampfbot.state.Config;

public class Output {

	public final static short ERROR = 0;
	public final static short INFO = 1;
	public final static short DEBUG = 2;

	public final static String DIR_LOG = "/logs/";
	public final static String DIR_HTML = "/html/";

	protected static Output instance = null;

	protected boolean htmlOutput = false;
	protected String htmlPath = null;
	protected BlockingQueue<DoItLater> actionQueue;

	/**
	 * Ausgabestufe die von der Outputklasse benutzt wird
	 */
	public static short level = DEBUG;

	public Output() {
		instance = this;

		// Check for HTML-Output
		if (Config.getHtmlPath() != null) {
			checkValidHtmlPath();
		}
	}

	public static boolean isHtmlOutput() {
		return getInstance().htmlOutput;
	}

	public static void generateIndex() {
		add(new IndexFile(getInstance().htmlPath));
	}

	public static void addLog(JSONObject log) {
		add(new LogFile(log, getInstance().htmlPath + Output.DIR_LOG));
		generateIndex();
	}

	public static void addKampf(JSONObject fight, JSONObject data, String method) {
		if (isHtmlOutput()) {
			try {

				// Taktik speichern
				add(new TacticsLogFile(getInstance().htmlPath, data));
				
				// Dateinamen generieren
				String filename = String.valueOf(new Date().getTime())
						+ fight.getJSONObject("opponent").getString("name")
								.replaceAll("[^a-zA-Z0-9]", "_") + ".html";

				fight.put("fightData", data);

				// Kampf-Daten schreiben
				add(new KampfFile(fight, getInstance().htmlPath
						+ Output.DIR_HTML + filename));

				// Log generieren
				JSONObject log = LogFile.getLog(method, fight.getJSONObject(
						"opponent").getString("name"));

				log.put("file", "." + DIR_HTML + filename);
				log.put("good", fight.getJSONObject("results").getBoolean(
						"fightWasWon"));

				// Log schreiben
				Output.addLog(log);
			} catch (JSONException e) {
			}
		}
	}

	public static void addGolden(JSONObject fight) {
		if (isHtmlOutput()) {
			try {

				// Dateinamen generieren
				String filename = String.valueOf(new Date().getTime())
						+ fight.getJSONObject("opponent").getString("name")
								.replaceAll("[^a-zA-Z0-9]", "_") + ".html";

				// Kampf-Daten schreiben
				add(new GoldenFile(fight, getInstance().htmlPath
						+ Output.DIR_HTML + filename));

				// Log generieren
				JSONObject log = LogFile.getLog("Golden", fight.getJSONObject(
						"opponent").getString("name"));

				log.put("file", "." + DIR_HTML + filename);
				log.put("good", fight.getJSONObject("results").getBoolean(
						"fightWasWon"));

				// Log schreiben
				Output.addLog(log);
			} catch (JSONException e) {
			}
		}
	}

	protected static void add(DoItLater c) {
		if (isHtmlOutput()) {
			getInstance().actionQueue.add(new DoItLater() {

				@Override
				public void doIt() {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
			});
			getInstance().actionQueue.add(c);
		}
	}

	private void checkValidHtmlPath() {
		File path = new File(Config.getHtmlPath());
		File logPath = new File(path.getAbsoluteFile() + DIR_LOG);
		File htmlPath = new File(path.getAbsoluteFile() + DIR_HTML);

		if ((!path.isDirectory() && !path.mkdirs())
				|| (!logPath.isDirectory() && !logPath.mkdirs())
				|| (!htmlPath.isDirectory() && !htmlPath.mkdirs())) {
			Output.printTabLn("HTML-Ausgabe konnte nicht gestartet werden",
					Output.ERROR);
			this.htmlOutput = false;
		} else {
			this.htmlOutput = true;
			this.htmlPath = path.getAbsolutePath();
			this.actionQueue = new LinkedBlockingDeque<DoItLater>();
			(new Thread(new HtmlWorker())).start();
		}
	}

	protected static Output getInstance() {
		if (instance == null) {
			new Output();
		}
		return instance;
	}

	/**
	 * Gibt eine Fehlermeldung und einen Stacktrace aus.
	 * 
	 * @param e
	 *            Exception zum Anzeigen
	 */
	public static void error(Exception e) {
		String message = "Es trat in unbehandelbarer Fehler auf:\n"
				+ e.getMessage() + "\n";
		message += "Dies kann viele Gründe haben. Sollte dieser Fehler sehr oft auftreten, melden Sie ihn bitte mit folgenden Trace:\n\n";
		StackTraceElement[] trace = e.getStackTrace();
		for (StackTraceElement elem : trace) {
			message += elem.toString() + "\n";
		}
		print(message + "\n\nVersion: " + Control.version + "\n\n", 0);
	}

	/**
	 * Gibt die Hilfe aus und beendet das Programm
	 */
	public static void help() {
		showLogo();
		System.out
				.print("\n\nBKampfBot - Version: "
						+ Control.version
						+ "\n"
						+ "\n"
						+ "Aufruf: BKampfBot.jar [Optionen]\n"
						+ "        BKampfBot.jar [Optionen] Tag-Modi [Tag-Modi]\n"
						+ "        BKampfBot.jar [Optionen] lotto\n"
						+ "        BKampfBot.jar [Optionen] lotto=N,N,N,N,N\n"
						+ "        BKampfBot.jar [Optionen] pins=PINARRAY\n"
						+ "        BKampfBot.jar [Optionen] testproxy"
						+ "\n"
						+ "Optionen:\n"
						+ "  --help  -h     		Zeigt diese Hilfe an\n"
						+ "  --config=FILE  		Nimmt FILE als Konfiguration\n"
						+ "\n"
						+ "Tag-Modi:\n"
						+ "  quiz           Tagesquiz\n"
						+ "  quiz=N         Tagesquiz mit N falsch\n"
						+ "  los            Rubbellos\n"
						+ "  spiel          Tagesspiel\n"
						+ "  wein           Weinkeller\n"
						+ "  glueck         Glücksrad\n"
						+ "\n"
						+ "Weitere Hinweise auf http://bundeskampf.georf.de\n\n"
						+ "BKampfBot ist freie Software, die Sie unter bestimmten Bedingungen weitergeben dürfen.\n");
		System.exit(0);
	}

	public static boolean check(int l) {
		return (l <= level);
	}

	public static void println(String string, int l) {
		if (check(l)) {
			toTerminal(string + "\n", l);
		}
	}

	public static void print(String string, int l) {
		if (check(l)) {
			toTerminal(string, l);
		}
	}

	public static void printClockLn(String string, int l) {
		if (check(l)) {
			toTerminal(getTime() + " " + string + "\n", l);
		}
	}

	public static void printClock(String string, int l) {
		if (check(l)) {
			toTerminal(getTime() + " " + string, l);
		}
	}

	public static void printTabLn(String string, int l) {
		if (check(l)) {
			toTerminal("      " + string + "\n", l);
		}
	}

	public static void printTab(String string, int l) {
		if (check(l)) {
			toTerminal("      " + string, l);
		}
	}

	protected static void toTerminal(String string, int l) {
		getInstance()._toTerminal(string, l);
	}

	protected void _toTerminal(String string, int l) {
		if (l == 0) {
			System.err.print(string);
		} else {
			System.out.print(string);
		}
	}

	public static void sleep(int deciSec, int l) {

		if (deciSec < 600) {

			printTabLn("Schlafe " + (deciSec / 10) + " Sekunden", l);

		} else {

			Calendar end = new GregorianCalendar();
			end.setTime(Config.getDate());
			end.add(Calendar.MILLISECOND, deciSec * 100);

			printTabLn("Schlafe " + Math.round(deciSec / 600) + " min (bis "
					+ getTime(end.getTime()) + " Uhr)", l);
		}
	}

	/**
	 * Gibt die aktuelle Zeit in "24:00" zurück.
	 * 
	 * @return String Zeit in 24h
	 */
	public static String getTime() {
		return getTime(Config.getDate());
	}

	public static String getTime(Date d) {
		return DateFormat.getTimeInstance(DateFormat.SHORT).format(d);
	}

	public static void showLogo() {
		System.out.print(" ____    _  __                      __ \n"
				+ "|  _  \\ | |/ /                     / _|\n"
				+ "| | | | | ' / __ _ _ __ ___  _ __ | |_\n"
				+ "| | | | |  < / _` | '_ ` _ \\| '_ \\|  _|\n"
				+ "| |_| | | . \\ (_| | | | | | | |_) | |\n"
				+ "|     / |_|\\_\\__,_|_| |_| |_| .__/|_|\n"
				+ "|   -<   ____        _      | |\n"
				+ "|  _  \\ |  _ \\      | |     |_|\n"
				+ "| | | | | |_) | ___ | |_\n" + "| | | | |  _ < / _ \\| __|\n"
				+ "| |_| | | |_) | (_) | |_\n"
				+ "|_____/ |____/ \\___/ \\__|\n");

	}

	public static void setLevel(int l) {
		switch (l) {

		case 0:
			level = ERROR;
			break;

		case 1:
			level = INFO;
			break;

		case 2:
			level = DEBUG;
			println("Ausgabe auf DEBUG gesetzt.", 2);
			break;
		}

	}

	protected class HtmlWorker implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {

					// take next action and handle it
					DoItLater action = actionQueue.take();

					action.doIt();
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
