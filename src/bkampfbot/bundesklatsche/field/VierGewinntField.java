package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.output.Output;

public class VierGewinntField extends Field {

	public VierGewinntField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {

		if (result.getJSONObject("char").getString("viergewinnt").equals("4")) {

			Output.printTabLn("Nicht implementiert: "
					+ this.getClass().getSimpleName(), Output.ERROR);
			Output.printTabLn("Vier gewinnt steht auf 4", Output.INFO);
			return false;
		}
		return true;
	}

}
