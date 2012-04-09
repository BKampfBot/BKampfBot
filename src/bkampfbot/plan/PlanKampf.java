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
import bkampfbot.exceptions.BadOpponent;
import bkampfbot.exceptions.ConfigError;
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.NotFound;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.state.Opponent;
import bkampfbot.utils.AngriffOptions;
import bkampfbot.utils.Keilerei;

/**
 * PlanKampf benötigt folgende Konfiguration: {"Kampf":"ID des Gegners"} oder
 * {"Kampf":"Name des Gegners"}
 * 
 * @author georf
 * 
 */
public final class PlanKampf extends PlanObject {
	private String idOrName;
	private AngriffOptions options;

	public PlanKampf(JSONObject help, Object name) throws FatalError {
		super("Kampf");

		if (name != null && name instanceof String) {
			idOrName = (String) name;
			options = new AngriffOptions(new JSONObject());
		} else {

			try {
				options = new AngriffOptions(help);

				idOrName = help.getString("Gegner");

			} catch (JSONException t) {
				throw new ConfigError("Kampf");
			}
		}
	}

	public final void run() throws RestartLater, FatalError {
		printJump("(" + idOrName + ")");

		if (!Utils.fightAvailable(15)) {
			return;
		}

		Opponent opponent;
		try {
			opponent = Opponent.getById(idOrName);
		} catch (NotFound e) {
			try {
				opponent = Opponent.getByName(idOrName);
			} catch (NotFound g) {
				Output.printTabLn(
						"Kann " + idOrName + " nicht finden, Abbruch",
						Output.INFO);
				return;
			}
		}

		try {
			Keilerei.fight(opponent, "Kampf", options, this);
		} catch (BadOpponent o) {
			Output.printTabLn("Angriff abgebrochen. "
					+ "Vermutlich heute zu viele Kämpfe gegen diesen Gegner.",
					1);
		}
	}
}
