package bkampfbot.bundesklatsche.field;

import json.JSONObject;
import bkampfbot.output.Output;

public class VierGewinntField extends Field {

	public VierGewinntField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() {

		// TODO Nicht implementiert

		Output.printTabLn("Nicht implementiert: "
				+ this.getClass().getSimpleName(), Output.ERROR);
		return false;
	}

}
