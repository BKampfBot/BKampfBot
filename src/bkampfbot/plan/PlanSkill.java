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
import bkampfbot.exceptions.FatalError;
import bkampfbot.output.Output;
import bkampfbot.utils.Skill;

/**
 * PlanSkill benötigt folgende Konfiguration: {"Skill":{"Kategorie":"Mukkies",
 * "Anzahl":1}}
 * 
 * @author georf
 * 
 */
public final class PlanSkill extends PlanObject {
	private String category = "";
	private int count = -1;
	private boolean bank = false;

	public PlanSkill(JSONObject object) throws FatalError {
		this.setName("Skill");

		try {
			JSONObject skill = object.getJSONObject(getName());
			category = skill.getString("Kategorie");

			try {
				count = skill.getInt("Anzahl");
			} catch (JSONException e) {
			}

			try {
				bank = skill.getBoolean("Bank");
			} catch (JSONException e) {

			}

		} catch (JSONException e) {
			throw new FatalError("Config error: Skill config is bad");
		}
	}

	public final void run() throws FatalError {
		Output.printClockLn("-> Skill (" + category + ")", Output.INFO);

		Skill.get(category).buy(count, bank);
	}

	@Override
	public boolean isPreRunningable() {
		return true;
	}
}
