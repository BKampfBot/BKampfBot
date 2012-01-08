package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.Utils;

public class StartField extends Field {

	public StartField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {
/*
 * 
        "payout_arr": {
            "gold": 7384,
            "bonus": 0,
            "atts": 0
        },
 */
		if (result.getInt("payout") == 1) {
			Utils.getString("bundesklatsche/get_data/2");
			return true;
		}
		return false;
	}

}
