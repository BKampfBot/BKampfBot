package bkampfbot.bundesklatsche;

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
