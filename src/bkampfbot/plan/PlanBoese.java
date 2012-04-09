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
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import bkampfbot.Utils;
import bkampfbot.exceptions.BadOpponent;
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.state.Opponent;
import bkampfbot.utils.AngriffOptions;
import bkampfbot.utils.Keilerei;

/**
 * PlanBoese benötigt folgende Konfiguration: {"Boese":{}}
 * 
 * @author georf
 * 
 */

public abstract class PlanBoese extends PlanObject {
	private int moneyAgain = -1;
	protected final AngriffOptions options;
	private boolean random = false;

	// private static ConcurrentHashMap<Integer, Opponent> list;

	public PlanBoese(JSONObject help, String name) throws FatalError {
		super(name);

		options = new AngriffOptions(help);

		try {
			this.moneyAgain = help.getInt("nochmal");
		} catch (JSONException r) {
		}

		try {
			this.random = help.getBoolean("Zufall");
		} catch (JSONException r) {
		}

		// GET /fights/enemysListJson HTTP/1.1

		// {"list":[{"attack":"\/fights\/start\/100249","profil":"\/characters\/profile\/AABEHN","id":"100249","signup_id":"100249","race":"Meck.-Vorp.","name":"hali","level":"29","hp":"614816"}],"nexturl":null,"prevurl":null}
		// {"list":[{"attack":"\/fights\/start\/163988","profil":"\/characters\/profile\/AGELLM","id":"163988","signup_id":"163988","race":"Meck.-Vorp.","name":"alfMeier","level":"20","hp":"388186"},{"attack":"\/fights\/start\/149359","profil":"\/characters\/profile\/AEKFIN","id":"149359","signup_id":"149359","race":"Meck.-Vorp.","name":"!!!!!Stampras!!","level":"21","hp":"429425"},{"attack":"\/fights\/start\/100249","profil":"\/characters\/profile\/AABEHN","id":"100249","signup_id":"100249","race":"Meck.-Vorp.","name":"hali","level":"29","hp":"614873"}],"nexturl":null,"prevurl":null}
	}

	protected abstract String getEnemyListUri();

	protected abstract OpponentList getOpponentList();

	protected abstract Opponent getOpponent();

	protected abstract void setOpponent(Opponent l);

	public final void run() throws FatalError, RestartLater {
		printJump();

		// check for status (1.0.beta6)
		if (!Utils.fightAvailable(15)) {
			return;
		}

		Opponent toFight = getOpponent();
		boolean fromList = true;
		if (moneyAgain > 0 && toFight != null && toFight.canFight()) {
			fromList = false;
			try {
				Output.println(" (nochmal)", Output.INFO);

				int money = Keilerei.fight(toFight, "Böse", options, this);
				toFight.addFight();

				if (money < this.moneyAgain) {
					setOpponent(null);
				}

			} catch (BadOpponent opp) {
				setOpponent(null);
				toFight.setDone();
				fromList = true;

				printJump();
			}

		}

		if (fromList) {
			Output.println("", Output.INFO);

			try {
				boolean result = false;
				for (int i = 0; i < 3 && !result; i++) {
					if (i != 0)
						Output.printTabLn(
								"Keinen Gegner gefunden. Versuche erneut.", 2);
					result = completeFight();
				}
				if (!result)
					Output.printTabLn(
							"Breche Kämpfen ab. Finde keine passenden Gegner.",
							2);
			} catch (NoList l) {
				Output.printTabLn("Keine Liste verfügbar.", 2);
			} catch (BadOpponent o) {
				Output.printTabLn(
						"Breche Kämpfen ab. Finde keine passenden Gegner.", 2);
			}
		}
	}

	private final boolean completeFight() throws NoList, BadOpponent,
			FatalError, RestartLater {
		Utils.visit("fights/start");

		try {

			JSONArray arr = getList();
			JSONObject now;

			// If list is available
			if (arr.length() > 0) {

				// unselect all opponents
				getOpponentList().unselectAll();

				for (int i = 0; i < arr.length(); i++) {
					now = arr.getJSONObject(i);
					int id = Integer.parseInt(now.getString("attack")
							.replaceAll("[^0-9]", ""));

					// not in list => add
					if (!getOpponentList().contains(id)) {
						Output.printTabLn("Füge " + now.getString("name")
								+ " zur Liste hinzu", 2);
						getOpponentList().put(id, now.getString("name"),
								now.getString("attack"));
					}
				}

				// delete all others
				getOpponentList().deleteUnselected();

				int nextKey = 0;

				// From up to down?

				if (!random) {

					int fights = -1;

					// go through the list
					for (int key : getOpponentList().keySet()) {
						if (fights == -1
								&& getOpponentList().get(key).canFight()) {
							nextKey = key;
							fights = getOpponentList().get(key).getFights();
						}
						if (getOpponentList().get(key).canFight()
								&& getOpponentList().get(key).getFights() < fights) {
							nextKey = key;
							fights = getOpponentList().get(key).getFights();
						}
					}

					if (fights == -1) {
						throw new BadOpponent(null);
					}
				} else {
					Integer[] keys = getOpponentList().keySet().toArray(
							new Integer[0]);
					Random generator = new Random();
					nextKey = keys[generator.nextInt(keys.length)];
				}

				try {
					int money = Keilerei.fight(getOpponentList().get(nextKey),
							"Böse", options, this);
					getOpponentList().get(nextKey).addFight();

					if (money >= this.moneyAgain) {
						setOpponent(getOpponentList().get(nextKey));
					} else {
						setOpponent(null);
					}

				} catch (BadOpponent e) {
					getOpponentList().get(nextKey).setDone();
					return false;
				}

				return true;
			} else {
				throw new NoList();
			}
		} catch (JSONException e) {
			Output.println(e.getMessage(), 0);
		}
		return false;
	}

	private final JSONArray getList() throws NoList {
		Output.printTabLn("Hole Bösespielerliste", 2);

		JSONArray ret = new JSONArray();
		try {
			String nexturl = "/fights/enemysListJson" + getEnemyListUri();

			while (true) {
				JSONObject ob = Utils.getJSON(nexturl);

				JSONArray list = ob.getJSONArray("list");

				for (int i = 0; i < list.length(); i++) {
					ret.put(list.get(i));
				}

				nexturl = ob.getString("nexturl");

				if (nexturl == null || nexturl.equals("null")) {
					return ret;
				}

			}

		} catch (JSONException e) {
			Output.printTabLn("Keine Bösespielerliste verfügbar", 1);
		}
		throw new NoList();
	}

	private final class NoList extends Exception {
		private static final long serialVersionUID = -2317742009841698364L;
	}

	/*
	 * protected final class Opponent { public final String attack; public final
	 * String name; public int fights = 0; public boolean canFight = true;
	 * public Calendar countDate; private boolean selected = true;
	 * 
	 * public Opponent(String attack, String name) { this.attack = attack;
	 * this.name = name; this.countDate = new GregorianCalendar();
	 * this.countDate.setTime(Config.getDate()); }
	 * 
	 * public final Opponent checkFightCounts() { Calendar today = new
	 * GregorianCalendar(); today.setTime(Config.getDate());
	 * today.set(Calendar.HOUR, 0); today.set(Calendar.MINUTE, 0);
	 * 
	 * if (this.countDate.before(today)) { this.countDate = new
	 * GregorianCalendar();
	 * 
	 * // Set all counters to zero this.fights = 0; this.canFight = true; }
	 * 
	 * return this; }
	 * 
	 * public void setSeleted() { selected = true; }
	 * 
	 * public void unsetSelected() { selected = false; }
	 * 
	 * public boolean selected() { return selected; } }
	 */
	protected final class OpponentList {
		private ConcurrentHashMap<Integer, Opponent> list;

		public OpponentList() {
			list = new ConcurrentHashMap<Integer, Opponent>();
		}

		public Opponent remove(int key) {
			return list.remove(key);
		}

		public Set<Integer> keySet() {
			ArrayList<Integer> l = new ArrayList<Integer>(list.keySet());
			Collections.shuffle(l);

			return new HashSet<Integer>(l);
		}

		public int size() {
			return list.size();
		}

		public Opponent put(int key, String name, String value) {
			Opponent opp = new Opponent(name, value);
			opp.select();
			return list.put(key, opp);
		}

		public Opponent get(int key) {
			return list.get(key);
		}

		public boolean contains(int key) {
			if (list.containsKey(key)) {
				Opponent opp = list.get(key);
				opp.checkNewDay();
				opp.select();

				return true;
			} else {
				return false;
			}
		}

		public ConcurrentHashMap<Integer, Opponent> getList() {
			return list;
		}

		public void unselectAll() {
			for (Integer opp : list.keySet()) {
				list.get(opp).unselect();
			}
		}

		public void deleteUnselected() {
			for (Integer opp : list.keySet()) {
				if (!list.get(opp).selected()) {
					list.remove(opp);
				}
			}
		}
	}
}
