package bkampfbot.bundesklatsche;

import json.JSONException;
import json.JSONObject;
import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanAngriff;
import bkampfbot.plan.PlanBundesklatsche;
import bkampfbot.state.Config;
import bkampfbot.state.User;

public class KampfField extends Field {

	private int fightsToDo = 3;
	private String race = null;
	private String fieldName = null;

	public KampfField(PlanBundesklatsche klatsche, int fights, String fieldName) {
		super(klatsche);
		fightsToDo = fights;
		this.fieldName = fieldName;
	}

	public KampfField(String race, PlanBundesklatsche klatsche) {
		super(klatsche);
		this.race = race;

		if (User.getRace().equals(race) || User.getRaceSecondary().equals(race)) {
			this.race = null;
		}

		this.fieldName = "Bundeslandfeld: " + race;
	}

	@Override
	public boolean action() throws JSONException, FatalError, RestartLater {

		Output.printClockLn(fieldName, Output.INFO);
		
		JSONObject angriff = new JSONObject();
		try {
			angriff = getConfig().getJSONObject("Angriff");
		} catch (JSONException e) {
			
		}

		if (this.race != null) {
			if (angriff.has("Land")) {
				angriff.remove("Land");
			}
			angriff.put("Land", race);
		}
		
		JSONObject config = new JSONObject();
		config.put("Angriff", angriff);

		int start = Integer.valueOf(getResult().getJSONObject("char")
				.getString("num2"));

		for (int i = start; i < fightsToDo; i++) {
			if (Config.getDebug()) {
				Output.printTabLn("Angriff " + (i + 1) + " von " + fightsToDo,
						Output.DEBUG);
			}

			PlanAngriff elem = new PlanAngriff(config);
			elem.setUseCache(false);
			elem.run();

			if (!elem.won()) {
				i--;
			}
		}
		return true;
	}

}
