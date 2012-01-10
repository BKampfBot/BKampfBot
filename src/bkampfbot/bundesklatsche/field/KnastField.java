package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.modes.Bundesklatsche;
import bkampfbot.output.Output;

public class KnastField extends Field {

	public KnastField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {

		/*
		 * "action": { "cont": "knast", "moveon": "0", "raus": 0 },
		 * 
		 * 
		 * "action": { "cont": "knast", "blackdice": 6, "beute": null, "raus":
		 * 1, "moveon": "1" },
		 */
		if (result.getJSONObject("action").getString("cont").equals("knast")) {
			for (int i = 0; i < 10; i++) {

				JSONObject action = result.getJSONObject("action");
				if (action.getInt("raus") == 1) {
					return true;
				}

				result = Bundesklatsche.rollTheDice();

			}

			Output.printTabLn("Beim Würfeln funktioniert etwas nicht. "
					+ this.getClass().getSimpleName(), Output.ERROR);
			return false;
		}
		return true;
	}

}
