package bkampfbot.bundesklatsche;

import bkampfbot.output.Output;
import json.JSONObject;

public class BahnhofField extends Field {

	public BahnhofField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() {
		Output.printClockLn("Bahnhof-Feld", Output.INFO);
		return true;
	}

}
