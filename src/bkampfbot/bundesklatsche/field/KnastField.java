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
    "action": {
        "cont": "knast",
        "moveon": "0",
        "raus": 0
    },
    
    
    "action": {
        "cont": "knast",
        "blackdice": 6,
        "beute": null,
        "raus": 1,
        "moveon": "1"
    },
    */
		
		for (int i = 0; i < 10; i++) {
			
			JSONObject action = result.getJSONObject("action");
			if (action.getInt("raus") == 1) {
				return true;
			}
			
			Bundesklatsche.rollTheDice();
			
		}
		
		// TODO Nicht implementiert

		Output.printTabLn("Nicht funktioniert: "
				+ this.getClass().getSimpleName(), Output.ERROR);
		return false;
	}

}
