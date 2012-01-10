package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.exception.FatalError;
import bkampfbot.modes.Jagd;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanAussendienst;
import bkampfbot.plan.PlanBank;
import bkampfbot.plan.PlanSkill;

public class AktionField extends Field {

	public AktionField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException, FatalError {
		
		/**
		 *  "action": {
        "cont": "",
        "done": 0,
        "gold": 159,
        "text": "Der Schwimmkurs im hiesigen Hallenbad fordert deine ganze Kraft, steigere deinen Fitnesswert um  1 Punkt!",
        "cancel": 1,
        "klatschen": 3
    },
		 */
		JSONObject action = result.getJSONObject("action");
		if (action.getString("text").equalsIgnoreCase("Der Schwimmkurs im hiesigen Hallenbad fordert deine ganze Kraft, steigere deinen Fitnesswert um  1 Punkt!")) {
			JSONObject config = new JSONObject("{\"Skill\":{\"Kategorie\":\"Fitness\",\"Anzahl\":1}}");
			PlanSkill skill = new PlanSkill(config);
			skill.run();
			return (skill.getBought() > 0);
		}
		if (action.getString("text").equalsIgnoreCase("Du bist im Jagdfieber, löse ein Wort bei der Wörterjagd!")) {
			Jagd jagd = new Jagd(1);
			jagd.run();
			return (jagd.getWordsSolved() > 0);
		}
		if (action.getString("text").equalsIgnoreCase("Du bist heute gut drauf in deiner Außendiensttätigkeit, absolviere einen leichten AD und kassiere einen Sonderbonus!")) {
			JSONObject config = new JSONObject("{\"Aussendienst\":0}");
			PlanAussendienst aussen = new PlanAussendienst(config);
			aussen.run();
			return true;
		}
		if (action.getString("text").equalsIgnoreCase("Die nächste Rate für deinen neuerworbenen Fernseher ist fällig, zahle 300 D-Mark auf dein Sparkassenkonto  ein um diese begleichen zu können!")) {
			PlanBank bank = new PlanBank(300);
			bank.run();
			return bank.inserted();
		}
		
		// TODO Nicht implementiert

		Output.printTabLn("Nicht implementiert: "
				+ this.getClass().getSimpleName(), Output.ERROR);
		Output.println(action.getString("text"), Output.INFO);
		return false;
	}

}
