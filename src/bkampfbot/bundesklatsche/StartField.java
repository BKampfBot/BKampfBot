package bkampfbot.bundesklatsche;

import json.JSONException;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class StartField extends Field {

	public StartField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {
		Output.printClockLn("Startfeld", Output.INFO);
		return true;
	}

}
