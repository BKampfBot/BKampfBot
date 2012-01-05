package bkampfbot.bundesklatsche.field;

import json.JSONObject;

public class OstBahnhofField extends Field {

	public OstBahnhofField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() {

		// Wir sind vom Nordbahnhof gekommen, wurden automatisch versetzt
		return true;
	}

}
