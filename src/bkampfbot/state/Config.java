package bkampfbot.state;

import java.util.Date;
import java.util.Random;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import bkampfbot.PlanManager;
import bkampfbot.exception.FatalError;
import bkampfbot.output.InfoFile;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanObject;

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

public class Config {
	/* obligatory */
	protected String userName;
	protected String userPassword;
	protected String host;

	/* opticaly */
	protected String configFile = "config.json";
	protected int outputLevel = 1;
	protected PlanManager plan0 = null;
	protected PlanManager plan1 = null;
	protected int fightAgain = 30;
	protected String infoPath = null;
	protected double sleepFactorMin = 1;
	protected double sleepFactorMax = 1;
	protected String userAgent = null;
	protected boolean lookAhead = true;
	protected int additionalService = 0;
	protected long dateDiff = 0;
	protected String proxyHost = null;
	protected int proxyPort = 0;
	protected String proxyUsername = null;
	protected String proxyPassword = null;
	protected int jagdMax = 0;
	protected int jagdMin = 0;
	protected boolean prevention = false;
	protected boolean debug = false;

	protected static Config instance = null;

	protected final static String[] userAgents = {
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en-us) AppleWebKit/xxx.x (KHTML like Gecko) Safari/12x.x",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.112 Safari/534.30",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16",
			"Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0",
			"Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0",
			"Mozilla/5.0 (X11; Linux x86_64; rv:5.0) Gecko/20100101 Firefox/5.0",
			"Mozilla/5.0 (Windows NT 5.1; rv:5.0.1) Gecko/20100101 Firefox/5.0.1",
			"Mozilla/5.0 (Windows NT 6.0; rv:5.0.1) Gecko/20100101 Firefox/5.0.1",
			"Mozilla/5.0 (Windows NT 6.0; WOW64; rv:5.0.1) Gecko/20100101 Firefox/5.0.1",
			"Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Mozilla/5.0 (Windows NT 6.0; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.5; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
			"Mozilla/5.0 (X11; U; Linux i686; de;) Gecko/20090202 Ubuntu/9.10 (karmic) Firefox/3.0.6",
			"Mozilla/5.0 (X11; U; Linux i686; de;) Gecko/20081215 Ubuntu/9.10 (karmic) Firefox/3.0.5",
			"Mozilla/5.0 (X11; U; Linux i686; de;) Gecko/20081111 Ubuntu/9.10 (karmic) Firefox/3.0.4",
			"Mozilla/5.0 (X11; U; Linux i686; de;) Gecko/20081007 Ubuntu/9.10 (karmic) Firefox/3.0.3",
			"Mozilla/5.0 (X11; U; Linux i686; de;) Gecko/20080922 Ubuntu/9.10 (karmic) Firefox/3.0.2", };

	public Config() {
		instance = this;

		userAgent = userAgents[(new Random()).nextInt(userAgents.length)];
	}

	public static void parseJsonConfig(JSONObject jsonConfig)
			throws JSONException, FatalError {
		String[] configKeys = JSONObject.getNames(jsonConfig);

		for (String key : configKeys) {

			// Benutzername
			if (key.equalsIgnoreCase("benutzername")) {

				setUserName(jsonConfig.getString(key));

				// Passwort
			} else if (key.equalsIgnoreCase("passwort")) {

				setUserPassword(jsonConfig.getString(key));

				// Ausgabe
			} else if (key.equalsIgnoreCase("passwort")) {

				setUserPassword(jsonConfig.getString(key));

				// Host
			} else if (key.equalsIgnoreCase("host")) {

				Config.setHost(jsonConfig.getString(key));

				// Angriff nochmal
			} else if (key.equalsIgnoreCase("angriff nochmal")) {

				Config.setFightAgain(jsonConfig.getInt(key));

				// Plan0
			} else if (key.equalsIgnoreCase("plan0")) {

				JSONArray planArray = jsonConfig.getJSONArray(key);
				PlanManager plan = new PlanManager(planArray.length());
				plan.setAussendienst();

				for (int i = 0; i < planArray.length(); i++) {
					plan.add(PlanObject.get(planArray.getJSONObject(i)));
				}

				setPlan0(plan);

				// Plan1
			} else if (key.equalsIgnoreCase("plan1")) {

				JSONArray planArray = jsonConfig.getJSONArray(key);
				PlanManager plan = new PlanManager(planArray.length());

				for (int i = 0; i < planArray.length(); i++) {
					plan.add(PlanObject.get(planArray.getJSONObject(i)));
				}

				setPlan1(plan);

				// Info Pfad
			} else if (key.equalsIgnoreCase("info pfad")) {

				setInfoPath(jsonConfig.getString(key));

				// Mehr Aussendienste
			} else if (key.equalsIgnoreCase("mehr aussendienste")) {

				setAdditionalService(jsonConfig.getInt(key));

				// Vorrausschauen
			} else if (key.equalsIgnoreCase("vorausschauen")) {

				setLookAhead(jsonConfig.getBoolean(key));

				// Zeitfaktor
			} else if (key.equalsIgnoreCase("zeitfaktor")) {

				try {
					double value = jsonConfig.getDouble(key);
					setSleepFactorMin(value);
					setSleepFactorMax(value);
				} catch (JSONException e) {
					JSONObject value = jsonConfig.getJSONObject(key);
					setSleepFactorMin(value.getDouble("min"));
					setSleepFactorMax(value.getDouble("max"));
				}

				// Useragent
			} else if (key.equalsIgnoreCase("useragent")) {

				setUserAgent(jsonConfig.getString(key));

			} else if (key.equalsIgnoreCase("proxy")) {
				JSONObject help = jsonConfig.getJSONObject(key);
				setProxyHost(help.getString("Host"));
				setProxyPort(help.getInt("Port"));

				try {
					setProxyUsername(help.getString("Username"));
					setProxyUsername(help.getString("Password"));
				} catch (JSONException e) {
				}

				// Useragent
			} else if (key.equalsIgnoreCase("jagd")) {
				try {
					JSONObject jagd = jsonConfig.getJSONObject(key);
					setJagdMin(jagd.getInt("min"));
					setJagdMax(jagd.getInt("max"));
				} catch (JSONException e) {
				}
				setUserAgent(jsonConfig.getString(key));
			} else if (key.equalsIgnoreCase("Verschleiern")) {
				setPrevention(jsonConfig.getBoolean(key));
			} else if (key.equalsIgnoreCase("Debug")) {
				setDebug(jsonConfig.getBoolean(key));
			}
		}
	}

	public static Config getInstance() {
		if (instance == null) {
			new Config();
		}

		return instance;
	}

	/**
	 * @return the configFile
	 */
	public static String getConfigFile() {
		return getInstance().configFile;
	}

	/**
	 * @param configFile
	 *            the configFile to set
	 */
	public static void setConfigFile(String configFile) {
		getInstance().configFile = configFile;
	}

	public static void setJagdMin(int int1) {
		getInstance().jagdMin = int1;
	}

	public static int getJagdMin() {
		return getInstance().jagdMin;
	}

	public static void setJagdMax(int int1) {
		getInstance().jagdMax = int1;
	}

	public static int getJagdMax() {
		return getInstance().jagdMax;
	}

	/**
	 * @return the host
	 */
	public static String getHost() {
		return getInstance().host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public static void setHost(String host) {
		if (host.length() > 0 && !host.substring(host.length() - 1).equals("/")) {
			host += "/";
		}
		getInstance().host = host;
	}

	/**
	 * @return the userName
	 */
	public static String getUserName() {

		return getInstance().userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public static void setUserName(String userName) {
		getInstance().userName = userName;
	}

	/**
	 * @return the userPassword
	 */
	public static String getUserPassword() {
		return getInstance().userPassword;
	}

	/**
	 * @param userPassword
	 *            the userPassword to set
	 */
	public static void setUserPassword(String userPassword) {
		getInstance().userPassword = userPassword;
	}

	/**
	 * @return the outputLevel
	 */
	public static int getOutputLevel() {
		return getInstance().outputLevel;
	}

	/**
	 * @param outputLevel
	 *            the outputLevel to set
	 */
	public static void setOutputLevel(int outputLevel) {

		if (outputLevel < 0 || outputLevel > 2) {
			Output.printTabLn("Das verwendete Ausgabelevel gibt es nicht: "
					+ outputLevel, Output.ERROR);
		} else {
			getInstance().outputLevel = outputLevel;
			Output.setLevel(outputLevel);
		}
	}

	/**
	 * @return the plan0
	 */
	public static PlanManager getPlan0() {
		return getInstance().plan0;
	}

	/**
	 * @param plan0
	 *            the plan0 to set
	 */
	public static void setPlan0(PlanManager plan0) {
		getInstance().plan0 = plan0;
	}

	/**
	 * @return the plan1
	 */
	public static PlanManager getPlan1() {
		return getInstance().plan1;
	}

	/**
	 * @param plan1
	 *            the plan1 to set
	 */
	public static void setPlan1(PlanManager plan1) {
		getInstance().plan1 = plan1;
	}

	/**
	 * @return the fightAgain
	 */
	public static int getFightAgain() {
		return getInstance().fightAgain;
	}

	/**
	 * @param fightAgain
	 *            the fightAgain to set
	 */
	public static void setFightAgain(int fightAgain) {
		getInstance().fightAgain = fightAgain;
	}

	/**
	 * @return the infoPath
	 */
	public static String getInfoPath() {
		return getInstance().infoPath;
	}

	/**
	 * @param infoPath
	 *            the infoPath to set
	 */
	public static void setInfoPath(String infoPath) {
		getInstance().infoPath = infoPath;

		InfoFile.initiate();
	}

	/**
	 * @return the sleepFactorMin
	 */
	public static double getSleepFactorMin() {
		return getInstance().sleepFactorMin;
	}

	/**
	 * @return the sleepFactorMax
	 */
	public static double getSleepFactorMax() {
		return getInstance().sleepFactorMax;
	}

	/**
	 * @param sleepFactorMin
	 *            the sleepFactorMin to set
	 */
	public static void setSleepFactorMin(double sleepFactorMin) {
		getInstance().sleepFactorMin = sleepFactorMin;
	}

	/**
	 * @param sleepFactorMax
	 *            the sleepFactorMax to set
	 */
	public static void setSleepFactorMax(double sleepFactorMax) {
		getInstance().sleepFactorMax = sleepFactorMax;
	}

	public static double getSleepFactor() {
		double min = getSleepFactorMin();
		double diff = getSleepFactorMax() - min;

		if (diff == 0) {
			return min;
		}

		return min + (new Random()).nextDouble() * diff;
	}

	public static String getProxyHost() {
		return getInstance().proxyHost;
	}

	public static void setProxyHost(String proxyHost) {
		getInstance().proxyHost = proxyHost;
	}

	public static int getProxyPort() {
		return getInstance().proxyPort;
	}

	public static void setProxyPort(int proxyPort) {
		getInstance().proxyPort = proxyPort;
	}

	public static String getProxyUsername() {
		return getInstance().proxyUsername;
	}

	public static void setProxyUsername(String proxyUsername) {
		getInstance().proxyUsername = proxyUsername;
	}

	public static String getProxyPassword() {
		return getInstance().proxyPassword;
	}

	public static void setProxyPassword(String proxyPassword) {
		getInstance().proxyPassword = proxyPassword;
	}

	/**
	 * @return the userAgent
	 */
	public static String getUserAgent() {
		return getInstance().userAgent;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public static void setUserAgent(String userAgent) {
		getInstance().userAgent = userAgent;
	}

	/**
	 * @return the lookAhead
	 */
	public static boolean isLookAhead() {
		return getInstance().lookAhead;
	}

	/**
	 * @param lookAhead
	 *            the lookAhead to set
	 */
	public static void setLookAhead(boolean lookAhead) {
		getInstance().lookAhead = lookAhead;
	}

	/**
	 * @return the additionalService
	 */
	public static int getAdditionalService() {
		return getInstance().additionalService;
	}

	/**
	 * @param additionalService
	 *            the additionalService to set
	 */
	public static void setAdditionalService(int additionalService) {
		if (additionalService > 3 || additionalService < 0) {
			Output.printTabLn("Maximal 3 zusätzliche Außendienste",
					Output.ERROR);
		} else {
			getInstance().additionalService = additionalService;
		}
	}

	public static void setLastDate(Date date) {
		getInstance().dateDiff = (new Date()).getTime() - date.getTime();
	}

	public static Date getDate() {
		Date now = new Date();
		now.setTime(now.getTime() - getInstance().dateDiff);
		return now;
	}

	public static boolean getPrevention() {
		return getInstance().prevention;
	}

	public static void setPrevention(boolean prevention) {
		getInstance().prevention = prevention;
	}

	public static void setDebug(boolean debug) {
		getInstance().debug = debug;
	}

	public static boolean getDebug() {
		return getInstance().debug;
	}
}
