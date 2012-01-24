package bkampfbot.bundesklatsche;

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
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class EreignisField extends Field {

	public EreignisField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {
		/*
		 * "action": { "cont": "", "text":
		 * "Auch wenn es Bogdan nicht gerne sieht: Du verkaufst Rubbellose zu Gunsten der Aktion Morgenwind. Deine Einnahmen erreichen bereits am ersten Tag:"
		 * , "bonus": "1", "gold": 1702, "klatschen": 5 },
		 */
		JSONObject action = getResult().getJSONObject("action");

		Output.printClock("Ereignis - ", Output.INFO);

		if (action.getString("bonus").equals("1")) {
			Output.print("gewonnen", Output.INFO);
		} else {
			Output.print("verloren", Output.INFO);
		}

		Output.println(" (" + action.getInt("gold") + " DM, "
				+ action.getInt("klatschen") + " Klatschen)", Output.INFO);
		Control.sleep(10);
		return true;
	}
}
