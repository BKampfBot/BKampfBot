package bkampfbot.bundesklatsche;

/*
 Copyright (C) 2011  georf@georf.de

 This file is part of BKampfBot.

 BKampfBot is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 2 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import json.JSONException;
import json.JSONObject;
import bkampfbot.Control;
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanAngriff;
import bkampfbot.plan.PlanBundesklatsche;
import bkampfbot.state.Config;
import bkampfbot.state.User;
import bkampfbot.utils.OpponentCache;

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
		Control.sleep(10);

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

		int start = Integer.valueOf(getResult().getJSONObject("char")
				.getString("num2"));

		// Dampfhammer einsetzen
		if (fightsToDo == 10
				&& start == 0
				&& getKlatsche()
						.useCardAsk(PlanBundesklatsche.CARD_DAMPFHAMMER)) {
			start = Integer.valueOf(getResult().getJSONObject("char")
					.getString("num2"));

		}

		// Stromschlag einsetzen
		/*
		 * if (fightsToDo == 3 && fieldName.equals("E-Werk")) {
		 * System.out.println("Stromschlag!!!!"); getKlatsche().printCards(); //
		 * System.exit(1); }
		 */

		int cacheLenght = 0;
		OpponentCache oc = OpponentCache.getInstance(race);
		
		for (int i = start; i < fightsToDo; i++) {
			if (Config.getDebug()) {
				Output.printTabLn("Angriff " + (i + 1) + " von " + fightsToDo,
						Output.DEBUG);
			}

			
			do {
				cacheLenght = oc.lenghtHighMoney();

				PlanAngriff elem = new PlanAngriff(angriff, race);
				elem.run();
				
	
				if (!elem.won()) {
					i--;
				}
			} while (oc.lenghtHighMoney() > cacheLenght);
		}
		return true;
	}
}
