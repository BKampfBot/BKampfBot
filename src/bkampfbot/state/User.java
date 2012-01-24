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

public class User {

	public enum Status {
		work, training, service, nothing
	}

	// Information about current user
	protected int maxLivePoints = 0;
	protected int currentLivePoints = 0;
	protected int level = 0;
	protected int gold = 0;

	public static int getGold() {
		return getInstance().gold;
	}

	public static void setGold(int gold) {
		getInstance().gold = gold;
	}

	protected String race = "";
	protected String raceSecondary = "xxx";
	protected Status status = Status.nothing;
	protected boolean guildMember = false;

	public static boolean isGuildMember() {
		return getInstance().guildMember;
	}

	public static void setGuildMember(boolean guildMember) {
		getInstance().guildMember = guildMember;
	}

	// User instance
	protected static User instance = null;

	public User() {
		instance = this;
	}

	protected static User getInstance() {
		if (instance == null) {
			new User();
		}

		return instance;
	}

	/**
	 * @return the maxLivePoints
	 */
	public static int getMaxLivePoints() {
		return getInstance().maxLivePoints;
	}

	/**
	 * @param maxLivePoints
	 *            the maxLivePoints to set
	 */
	public static void setMaxLivePoints(int maxLivePoints) {
		getInstance().maxLivePoints = maxLivePoints;
	}

	/**
	 * @return the currentLivePoints
	 */
	public static int getCurrentLivePoints() {
		return getInstance().currentLivePoints;
	}

	/**
	 * @param currentLivePoints
	 *            the currentLivePoints to set
	 */
	public static void setCurrentLivePoints(int currentLivePoints) {
		getInstance().currentLivePoints = currentLivePoints;
	}

	/**
	 * @return the level
	 */
	public static int getLevel() {
		return getInstance().level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public static void setLevel(int level) {
		getInstance().level = level;
	}

	/**
	 * @return the race
	 */
	public static String getRace() {
		return getInstance().race;
	}

	/**
	 * @param race
	 *            the race to set
	 */
	public static void setRace(String race) {
		race = race.trim();

		if (race.compareToIgnoreCase("Meck.-Vorp.") == 0) {
			setRaceSecondary("Mecklenburg-Vorpommern");
		} else if (race.compareToIgnoreCase("Bad.WÃ¼rtt.") == 0) {
			setRaceSecondary("Baden-Württemberg");
		} else if (race.compareToIgnoreCase("Rheinl-Pfalz") == 0) {
			setRaceSecondary("Rheinland-Pfalz");
		}
		// "Nordrhein-Westfalen"
		// "Hessen"
		// "Sachsen-Anhalt"
		// Thüringen -> OK

		getInstance().race = race;
	}

	/**
	 * @return the raceShort
	 */
	public static String getRaceSecondary() {
		return getInstance().raceSecondary;
	}

	/**
	 * @param raceShort
	 *            the raceShort to set
	 */
	public static void setRaceSecondary(String raceShort) {
		getInstance().raceSecondary = raceShort;
	}

	/**
	 * @return the status
	 */
	public static Status getStatus() {
		return getInstance().status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public static void setStatus(Status status) {
		getInstance().status = status;
	}
}
