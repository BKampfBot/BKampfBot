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

	private JSONObject lastResult = null;
	private JSONObject lastChar = null;
	private final JSONObject config;

	public PlanBundesklatsche(JSONObject config) throws FatalError {
		this.setName("Bundesklatsche");

		try {
			this.config = config.getJSONObject(getName());
		} catch (JSONException e) {
			throw new FatalError("Bundesklatsche ist falsch konfiguriert.");
		}
	}

	public void run() throws FatalError, RestartLater {
		Output.printClockLn("-> Bundesklatsche", Output.INFO);

		try {

			// first get info
			info(0);

			// is time for dice?
			dice();
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
				if (!next) next();

			} while (next || dice());
		} catch (JSONException e) {
			Output.error(e);
			return;
		} catch (DiceException e) {
			return;
		}
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
			throw new DiceException();
		}

		int maxRolls = lastChar.getInt("max_rolls");

		if (rolls >= maxRolls) {
			Output.printTabLn("Maximale Würfe des Tages erreicht", Output.INFO);
			throw new DiceException();
		}

		if (lastChar.getInt("rolls_ok") != 0) {
			rollAndOutputDice();
			return true;
		}
		Output.printTabLn("Nicht genügend Punkte zum Würfeln", Output.INFO);
		throw new DiceException();
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

	private static JSONObject getData(int type) throws JSONException {
		Control.sleep(5);
		return Utils.getJSON("bundesklatsche/get_data/" + type);
	}

	private void info(int type) throws JSONException {
		lastResult = getData(type);
		lastChar = lastResult.getJSONObject("char");

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

	private final class DiceException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2049167987518967561L;

	}

	public JSONObject getLastResult() {
		return lastResult;
	}

	public JSONObject getConfig() {
		return config;
	}
}
