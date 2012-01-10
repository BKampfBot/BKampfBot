package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.output.Output;

public class StartField extends Field {

	public StartField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {
		Output.printClockLn("Startfeld", Output.INFO);
		return true;
	}

}
