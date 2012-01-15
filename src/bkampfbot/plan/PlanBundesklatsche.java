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
import bkampfbot.Utils;
import bkampfbot.bundesklatsche.Field;
import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
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
		try {

			// first get info
			info(0);

			// is time for dice?
			dice();

			do {
				info(0);

				int pos = Integer.valueOf(lastChar.getString("figur_pos"));

				// Erzeuge Spielfeld
				Field current = Field.getField(pos, lastResult);

				// Führe Spielfeld aus
				if (!current.action()) {
					Output.printTabLn(
							"Konnte Spielfeld nicht vollständig abarbeiten.",
							Output.INFO);
					return;
				}

				// Bestätige Gewinn oder Verlust
				next();

			} while (dice());
		} catch (JSONException e) {
			Output.error(e);
			return;
		}
	}

	private boolean dice() throws JSONException {

		if (!lastChar.getString("cont").equals("rollthedice")) {
			return false;
		}

		int rolls = Integer.valueOf(lastChar.getString("rolls"));
		int maxRolls = lastChar.getInt("max_rolls");

		if (rolls < maxRolls && lastChar.getInt("rolls_ok") != 0) {
			info(1);

			Output.printClockLn("Würfel: "
					+ lastResult.getJSONObject("action").getInt("blackdice"),
					Output.DEBUG);
			return true;
		}
		return false;
	}

	public static JSONObject rollTheDice() throws JSONException {
		return getData(1);
	}

	private static JSONObject getData(int type) throws JSONException {
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
}
