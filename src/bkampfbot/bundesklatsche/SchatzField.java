package bkampfbot.bundesklatsche;

import json.JSONException;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

/**
 * Geschenke satt. Auf dem Schatzfeld gibt es DM und Klatschen für lau. Ihr müßt
 * nichts dafür tun.
 * 
 * @author georf
 * 
 */
public class SchatzField extends Field {

	public SchatzField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {

		Output.printClockLn("Schatz: "
				+ getResult().getJSONObject("char").getString("num1")
				+ " Mark, "
				+ getResult().getJSONObject("char").getString("num2")
				+ " Klatschen", Output.INFO);
		return true;
	}

}
