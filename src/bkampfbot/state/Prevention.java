package bkampfbot.state;

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

import java.util.Random;
import bkampfbot.Control;
import bkampfbot.Utils;

/**
 * Hilft bei der Verschleierung des Bots
 * 
 * Ruft in Abständen in sinnvoller Reihenfolge Urls auf. Wurde als Sinleton
 * implementiert.
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * @see Url
 */
public class Prevention {

	public final int MIN_SECONDS = 180;

	private Url[] urls;
	private Url last = null;

	private static Prevention instance = null;

	public Prevention() {
		urls = new Url[32];
		urls[0] = new Url("characters/index");
		urls[1] = new Url("placeOfHonour/index", 3, new Url("placeOfHonour/me"));
		urls[2] = new Url("placeOfHonour/index", 3, new Url("placeOfHonour/me",
				7, new Url("placeOfHonour/top", 9, new Url(
						"placeOfHonour/top/16"))));
		urls[3] = new Url("city/index", 5, new Url("sparkasse/banker"));
		urls[4] = new Url("punkte", 1, new Url("lotto/history"));
		urls[5] = new Url("punkte");
		urls[6] = new Url("punkte", 2, new Url("kiosk/", 1, new Url(
				"kiosk/getAktuellesGerichtData")));
		urls[7] = new Url("challenge/", 2, new Url("challenge/challengeData",
				1, new Url("challenge/checkBonus")));
		urls[8] = new Url("challenge/", 2, new Url("challenge/challengeData",
				1, new Url("challenge/checkBonus", 5, new Url(
						"challenge/achievements", 1, new Url(
								"challenge/getAchievements/0")))));
		urls[9] = new Url("city/index");
		urls[10] = new Url("pns");
		urls[11] = new Url("tournament/index/me/");
		urls[12] = new Url("placeOfHonour/index");
		urls[13] = new Url("verein/ehrenliste");
		urls[14] = new Url("challenge/");
		urls[15] = new Url("challenge/achievements");
		urls[16] = new Url("city/index", 3, new Url("items/index"));
		urls[17] = new Url("city/index", 4, new Url("arbeitsamt/index"));
		urls[18] = new Url("city/index", 3, new Url("arbeitsamt/index"));
		urls[19] = new Url("city/index", 4, new Url("training"));
		urls[20] = new Url("city/index", 3, new Url("kiosk"));
		urls[21] = new Url("bundesklatsche/index");
		urls[22] = new Url("bundesklatsche/index" , 4, new Url("bundesklatsche/bundeshelden"));
		urls[23] = new Url("bundesklatsche/index" , 4, new Url("bundesklatsche/klatschenfreunde"));
		urls[24] = new Url("bundesklatsche/index" , 4, new Url("bundesklatsche/klatschenmeister"));
		
		// Nur für Vereinsmitglieder
		urls[25] = (new Url("verein", 2, new Url("verein/shoutbox"))).setOnlyGuild();
		urls[26] = (new Url("verein", 2, new Url("verein/mitglieder"))).setOnlyGuild();
		urls[27] = (new Url("verein", 2, new Url("verein/gildenkasse"))).setOnlyGuild();
		urls[28] = (new Url("verein", 2, new Url("verein/gildensaal"))).setOnlyGuild();
		urls[29] = (new Url("verein", 2, new Url("verein/feindschaften"))).setOnlyGuild();
		urls[30] = (new Url("verein", 2, new Url("verein/buendnisse"))).setOnlyGuild();
		urls[31] = (new Url("verein", 2, new Url("guild_challenge/index"))).setOnlyGuild();
	}

	/**
	 * Singleton helper
	 * 
	 * @return Instance
	 */
	public static Prevention getInstance() {
		if (instance == null) {
			instance = new Prevention();
		}
		return instance;
	}

	/**
	 * Get next url
	 * 
	 * @return url as short (without domain) string
	 */
	private String getUrl() {
		if (last == null || !last.hasNext()) {
			last = getRandom();
		} else {
			last = last.getNext();
		}
		return last.getUrl();
	}

	/**
	 * Return a random {@link Url}
	 * 
	 * @return {@link Url}
	 */
	private Url getRandom() {
		int index;
		do {
			index = (new Random()).nextInt(urls.length);
		
		} while (!User.isGuildMember() && urls[index].isOnlyGuild());
		
		return urls[index];
	}

	/**
	 * 
	 * @return
	 */
	private int getDeciSeconds() {
		if (last == null || !last.hasNext()) {

			// less than min, but more than the half
			int half = (Math.round(MIN_SECONDS / 2) - 1) * 10;
			return (new Random()).nextInt(half) + half;

		} else {

			int sec = last.getSeconds();
			if ((new Random()).nextBoolean()) {
				return (sec * 10) + (new Random()).nextInt(sec * 4);
			} else {
				return (sec * 10) - (new Random()).nextInt(sec * 2);
			}
		}
	}

	public int doSomething(int deciSeconds) {

		// reset pointer
		last = null;

		while (deciSeconds > MIN_SECONDS) {

			int sleepDeciSeconds = getDeciSeconds();
			deciSeconds -= sleepDeciSeconds;

			// sleep
			Control.quietSleep(sleepDeciSeconds * 100);

			// rufe sinnlose URL auf
			Utils.getString(getUrl());
		}
		return deciSeconds;
	}
}
