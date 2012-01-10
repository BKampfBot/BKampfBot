package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.Utils;
import bkampfbot.output.Output;

public class HopTopField extends Field {

	public HopTopField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {
/*
		// wieviel einsatz ist möglich? maximal bestimmt die bisherige beute?

		// einsatz eingeben
		// bundesklatsche/set_hoptop/40

		JSONObject set = Utils.getJSON("bundesklatsche/set_hoptop/"
				+ result.getJSONObject("char").getInt("beute"));

		// rückgabe enthält srn
		// {"status":"ok","srn":7590}

		int code = Math.round(((set.getInt("srn") * 5 + 234) * 2 - 1104) / 5);
		// POST /bundesklatsche/fin_hoptop/1/15053 HTTP/1.1
		// RundenVon(((SicherheitsCode * 5 + 234) * 2 - 1104) / 5)

		try {
			Utils.getJSON("bundesklatsche/fin_hoptop/1/" + code);
			return true;
		} catch (JSONException e) {
			Output.printTabLn("Es gab einen Fehler bei HopTop", Output.ERROR);
			return false;
		}*/

		// TODO Nicht implementiert

		Output.printTabLn("Nicht implementiert: "
				+ this.getClass().getSimpleName(), Output.ERROR);
		return false;
	}

}
