package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.output.Output;

/**
 * Geschenke satt. Auf dem Schatzfeld gibt es DM und Klatschen für lau. Ihr müßt
 * nichts dafür tun.
 * 
 * @author georf
 * 
 */
public class SchatzField extends Field {

	public SchatzField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {

		Output.printClockLn(
				"Schatz: " + result.getJSONObject("char").getString("num1")
						+ " Mark, "
						+ result.getJSONObject("char").getString("num2")
						+ " Klatschen", Output.INFO);
		return true;
	}

}
