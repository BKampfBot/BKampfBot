package bkampfbot.bundesklatsche;

import json.JSONException;
import bkampfbot.Control;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class VierGewinntField extends Field {

	public VierGewinntField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {

		if (getResult().getJSONObject("char").getString("viergewinnt").equals(
				"4")) {

			Output.printTabLn("Nicht implementiert: "
					+ this.getClass().getSimpleName(), Output.ERROR);
			Output.printTabLn("Vier gewinnt steht auf 4", Output.INFO);
			return false;
		}
		Control.sleep(10);
		return true;
	}

}
