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

import bkampfbot.Utils;
import bkampfbot.exception.BadOpponent;
import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
import bkampfbot.output.Output;

import json.JSONException;
import json.JSONObject;

/**
 * PlanKampf benötigt folgende Konfiguration: {"Kampf":"ID des Gegners"} oder
 * {"Kampf":"Name des Gegners"}
 * 
 * @author georf
 * 
 */
public final class PlanKampf extends PlanObject {
	private String idOrName;
	private int medicine = -1;
	private boolean buyCrystal = false;

	public PlanKampf(JSONObject object) throws FatalError {
		this.setName("Kampf");

		try {
			this.idOrName = object.getString("Kampf");
		} catch (JSONException e) {

			try {
				JSONObject help = object.getJSONObject("Kampf");

				try {
					this.idOrName = help.getString("Gegner");
				} catch (JSONException r) {
				}

				try {
					this.medicine = help.getInt("Medizin");
				} catch (JSONException r) {
				}

				try {
					this.buyCrystal = help.getBoolean("Zwerg");
				} catch (JSONException r) {
				}

			} catch (JSONException t) {
				throw new FatalError("Config error: Kampf");
			}
		}
	}

	public final void run() throws FatalError, RestartLater {
		Output.printClockLn("-> Kampf (" + this.idOrName + ")", 1);

		if (!Utils.fightAvailable(15)) {
			return;
		}

		String attack = Utils.findAttackById(this.idOrName);
		if (attack == null) {
			attack = Utils.findAttackByName(this.idOrName);
		}

		if (attack == null) {
			Output.printTabLn(
					"Kann Namen nicht finden. Abbruch dieses Kampfes.", 1);
			return;
		}

		try {
			Utils.fight(attack, this.idOrName, "Kampf", medicine, this, buyCrystal);
		} catch (BadOpponent o) {
			Output.printTabLn("Angriff abgebrochen. "
					+ "Vermutlich heute zu viele Kämpfe gegen diesen Gegner.",
					1);
		}
	}
}
