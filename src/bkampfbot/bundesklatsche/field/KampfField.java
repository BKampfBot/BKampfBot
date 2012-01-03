package bkampfbot.bundesklatsche.field;

import json.JSONObject;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanAngriff;
import bkampfbot.state.Config;
import bkampfbot.state.User;

public class KampfField extends Field {

	private int fightsToDo = 3;
	private String race = null;

	public KampfField(JSONObject result) {
		super(result);
		// TODO Eventuell nicht immer 10?
		fightsToDo = 10;
	}

	public KampfField(String race, JSONObject result) {
		super(result);
		this.race = race;

		if (User.getRace().equals(race) || User.getRaceSecondary().equals(race)) {
			this.race = null;
		}
	}

	@Override
	public boolean action() {
		try {
			JSONObject config = new JSONObject();
			JSONObject angriff = new JSONObject();

			if (this.race != null) {
				angriff.put("Land", race);
			}
			angriff.put("Stufe", -3);
			angriff.put("Zwerg", true);

			config.put("Angriff", angriff);

			int start = Integer.valueOf(result.getJSONObject("char").getString(
					"num2"));

			for (int i = start; i < fightsToDo; i++) {
				if (Config.getDebug()) {
					Output.printTabLn("Angriff " + i + " von " + fightsToDo,
							Output.DEBUG);
				}

				PlanAngriff elem = new PlanAngriff(config);
				elem.run();
				if (!elem.won()) {
					i--;
				}
			}
			return true;
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			return false;
		}
	}

}
