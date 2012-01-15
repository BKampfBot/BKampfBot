package bkampfbot.bundesklatsche;

import json.JSONException;
import json.JSONObject;
import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class HopTopField extends Field {

	public HopTopField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {
		Output.printClockLn("HopTop-Feld", Output.INFO);

		JSONObject set = Utils.getJSON("bundesklatsche/set_hoptop/"
				+ getResult().getJSONObject("char").getInt("beute"));

		int code = Math.round(((set.getInt("srn") * 5 + 234) * 2 - 1104) / 5F);

		Control.sleep(150, Output.DEBUG);

		try {
			Utils.getJSON("bundesklatsche/fin_hoptop/1/" + code);
			return true;
		} catch (JSONException e) {
			Output.printTabLn("Es gab einen Fehler bei HopTop", Output.ERROR);
			return false;
		}
	}
}
