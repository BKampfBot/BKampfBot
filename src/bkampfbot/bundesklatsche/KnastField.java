package bkampfbot.bundesklatsche;

import json.JSONException;
import json.JSONObject;
import bkampfbot.Control;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class KnastField extends Field {

	public KnastField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {
		JSONObject result = getResult();
		if (result.getJSONObject("action").getString("cont").equals("knast")) {
			Output.printClockLn("In den Knast", Output.INFO);
			for (int i = 0; i < 10; i++) {

				JSONObject action = result.getJSONObject("action");
				if (action.getInt("raus") == 1) {
					return true;
				}

				result = getKlatsche().rollTheDice();

			}

			Output.printTabLn("Beim Würfeln funktioniert etwas nicht. "
					+ this.getClass().getSimpleName(), Output.ERROR);
			return false;
		} else {
			Output.printClockLn("Knast: Freiwurf", Output.INFO);
			Control.sleep(10);
			return true;
		}
	}

}
