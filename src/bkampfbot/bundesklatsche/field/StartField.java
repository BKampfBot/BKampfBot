package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.Utils;
import bkampfbot.output.Output;

public class StartField extends Field {

	public StartField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {
		Output.printClockLn("Startfeld", Output.INFO);
		
		if (result.getInt("payout") == 1) {
			Utils.getString("bundesklatsche/get_data/2");
		}
		return true;
	}

}
