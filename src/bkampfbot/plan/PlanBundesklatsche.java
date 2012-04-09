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

import json.JSONException;
import json.JSONObject;
import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.bundesklatsche.Field;
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.NextField;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.output.Output;

public final class PlanBundesklatsche extends PlanObject {
	public final static int CARD_KNASTRAUS = 1;
	public final static int CARD_BEUTESICHERN = 2;
	public final static int CARD_FREIEAUSWAHL = 3;
	public final static int CARD_FREIWURF = 4;
	public final static int CARD_NEUSTART = 5;
	public final static int CARD_WEITSPRUNG = 6;
	public final static int CARD_DAMPFHAMMER = 7;
	public final static int CARD_FAHRSCHEIN = 11;

	private final class DiceException extends Exception {
		private static final long serialVersionUID = -2049167987518967561L;
	}

	private static JSONObject getData(int type) throws JSONException {
		Control.sleep(5);
		return Utils.getJSON("bundesklatsche/get_data/" + type);
	}

	public static String id2Card(int id) {
		switch (id) {
		default:
			return "Unbekannt: " + id;
		case CARD_KNASTRAUS:
			return "Raus aus dem Knast";
		case CARD_BEUTESICHERN:
			return "Beute sichern";
		case CARD_FREIEAUSWAHL:
			return "Freie Auswahl";
		case CARD_FREIWURF:
			return "Freiwurf";
		case CARD_NEUSTART:
			return "Neustart";
		case CARD_WEITSPRUNG:
			return "Weitsprung";
		case CARD_DAMPFHAMMER:
			return "Dampfhammer";
		case CARD_FAHRSCHEIN:
			return "Rückfahrschein";
		}
	}

	private JSONObject lastResult = null;

	private JSONObject lastChar = null;

	private final JSONObject config;

	private int[] cards = { 0, 0, 0, 0, 0, 0, 0, 0 };

	public PlanBundesklatsche(JSONObject config) throws FatalError {
		super("Bundesklatsche");

		this.config = config;
	}

	private boolean dice() throws JSONException, DiceException {

		if (!lastChar.getString("cont").equals("rollthedice")) {
			return false;
		}

		int maxRollsConfig = Integer.MAX_VALUE;
		try {
			maxRollsConfig = config.getInt("Wuerfe");
		} catch (JSONException e) {
		}

		int rolls = Integer.valueOf(lastChar.getString("rolls"));

		if (rolls >= maxRollsConfig) {
			Output.printTabLn("Maximale Würfe erreicht", Output.INFO);
			return useDiceCards();
		}

		int maxRolls = lastChar.getInt("max_rolls");

		if (rolls >= maxRolls) {
			Output.printTabLn("Maximale Würfe des Tages erreicht", Output.INFO);
			return useDiceCards();
		}

		if (lastChar.getInt("rolls_ok") != 0) {
			rollAndOutputDice();
			return true;
		}
		Output.printTabLn("Nicht genügend Punkte zum Würfeln", Output.INFO);
		return useDiceCards();
	}

	/**
	 * Versucht nochmal Würfe frei zu machen. Dafür wird die Freiwurf oder die
	 * Neustartkarte eingesetzt, falls eine vorhanden ist. Ansonsten komme eine
	 * DiceException
	 * 
	 * @return
	 * @throws DiceException
	 * @throws JSONException
	 */
	private boolean useDiceCards() throws DiceException, JSONException {
		if (useCardAsk(CARD_FREIWURF)) {
			rollAndOutputDice();
			return true;
		} else if (useCardAsk(CARD_NEUSTART)) {
			rollAndOutputDice();
			return true;
		} else {
			throw new DiceException();
		}
	}

	public boolean useCard(int id) throws JSONException {

		// Würfel eine 2 (id = 6)
		// POST /bundesklatsche/get_data/0/3/2 HTTP/1.1\r\n

		// Freiwurf (id = 4)
		// POST /bundesklatsche/get_data/0/1

		// refresh
		info(0);

		int field = getCard(id);

		if (field == 0) {
			return false;
		}

		Utils.getString("bundesklatsche/get_data/0/" + field);

		Output.printTabLn("Benutze " + id2Card(id), Output.INFO);

		// refresh
		info(0);
		return true;
	}

	public boolean useCardAsk(int id) throws JSONException {
		boolean use = true;

		try {
			use = getConfig().getBoolean("Karten");
		} catch (JSONException e) {
		}

		return (use && useCard(id));
	}

	public boolean buyCard() {
		/*
		 * POST /bundesklatsche/buy_card/ HTTP/1.1\r\n
		 * 
		 * { "char": { "character_id": "744132", "race_id": "12", "rolls": "6",
		 * "roll_date": "2012-01-30", "beute": "687", "sicher": "2570",
		 * "klatschen": "76", "meister": "0", "figur_pos": "13", "cont":
		 * "rollthedice", "num1": "0", "num2": "0", "var1": "", "sknum1": "0",
		 * "skvar1": "", "viergewinnt": "3", "sk1": 3, "sk2": "0", "sk3": "0",
		 * "sk4": "0", "sk5": "0", "sk6": "0", "sk7": "0", "sk8": "0",
		 * "backpfeifen": 20, "last_sicher": "0", "last_klatschen": "49",
		 * "last_meister": "0", "last_payout": "19", "last_round": "19",
		 * "modified": "2012-01-30 02:13:45", "round": 20, "run": true },
		 * "card_id": 3
		 */
		return false;
	}

	public int getCard(int id) {
		for (int i = 0; i < cards.length; i++) {
			if (id == cards[i]) {
				return i + 1;
			}
		}
		return 0;
	}

	public JSONObject getConfig() {
		return config;
	}

	public JSONObject getLastResult() {
		return lastResult;
	}

	private void info(int type) throws JSONException {

		Control.sleep(3);

		lastResult = getData(type);
		lastChar = lastResult.getJSONObject("char");

		// set cards
		for (int i = 0; i < cards.length; i++) {
			try {
				cards[i] = lastChar.getInt("sk" + (i + 1));
			} catch (JSONException e) {
				try {
					cards[i] = Integer.valueOf(lastChar.getString("sk"
							+ (i + 1)));
				} catch (JSONException g) {
					cards[i] = 0;
				}
			}
		}

		if (lastResult.getInt("payout") == 1) {
			info(2);
		}
	}

	private void next() throws JSONException {
		// aktualisieren
		info(0);

		// könnte man würfeln?
		if (lastChar.getString("cont").equals("rollthedice")) {
			return;
		}

		// weiter
		info(1);
	}

	public void printCards() {
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != 0) {
				Output.printTabLn("  " + id2Card(cards[i]), Output.DEBUG);
			}
		}
	}

	public void rollAndOutputDice() throws JSONException {

		info(1);

		Output.printClockLn("Würfel: "
				+ lastResult.getJSONObject("action").getInt("blackdice"),
				Output.DEBUG);
	}

	public JSONObject rollTheDice() throws JSONException {

		return getData(1);
	}

	public void run() throws FatalError, RestartLater {
		printJump();

		try {

			// first get info
			info(0);

			// is time for dice?
			dice();

			// print info about cards
			printCards();

			boolean next = false;
			do {
				next = false;

				info(0);

				int pos = Integer.valueOf(lastChar.getString("figur_pos"));

				// Erzeuge Spielfeld
				Field current = Field.getField(pos, this);

				try {
					// Führe Spielfeld aus
					if (!current.action()) {
						Output
								.printTabLn(
										"Konnte Spielfeld nicht vollständig abarbeiten.",
										Output.INFO);
						return;
					}
				} catch (NextField e) {
					next = true;
				}
				Control.sleep(10);

				// Bestätige Gewinn oder Verlust
				if (!next)
					next();

			} while (next || dice());
		} catch (JSONException e) {
			Output.error(e);
			return;
		} catch (DiceException e) {
			return;
		}
	}
}
