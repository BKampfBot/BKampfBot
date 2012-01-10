package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanAngriff;
import bkampfbot.state.Config;
import bkampfbot.state.User;

public class KampfField extends Field {

	private int fightsToDo = 3;
	private String race = null;
	
	public KampfField(JSONObject result, int fights) {
		super(result);
		fightsToDo = 3;
	}

	public KampfField(String race, JSONObject result) {
		super(result);
		this.race = race;

		if (User.getRace().equals(race) || User.getRaceSecondary().equals(race)) {
			this.race = null;
		}
	}

	@Override
	public boolean action() throws JSONException, FatalError, RestartLater {

		JSONObject config = new JSONObject();
		JSONObject angriff = new JSONObject();

		if (this.race != null) {
			angriff.put("Land", race);
		}
		angriff.put("Stufe", -4);
		angriff.put("Zwerg", true);
		angriff.put("Respekt", new JSONObject("{\"min\":-1,\"max\":70000}"));

		config.put("Angriff", angriff);

		int start = Integer.valueOf(result.getJSONObject("char").getString(
				"num2"));

		for (int i = start; i < fightsToDo; i++) {
			if (Config.getDebug()) {
				Output.printTabLn("Angriff " + (i + 1) + " von " + fightsToDo,
						Output.DEBUG);
			}

			PlanAngriff elem = new PlanAngriff(config);
			elem.setUseCache(false);
			elem.run();

			if (!elem.won()) {
				Output.println("Setze eins zurück: " + i, Output.DEBUG);
				i--;
			}
		}
		return true;
	}

}
