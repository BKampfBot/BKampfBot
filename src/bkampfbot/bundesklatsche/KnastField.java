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
import bkampfbot.exceptions.NextField;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class KnastField extends Field {

	public KnastField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException, NextField {
		JSONObject result = getResult();
		if (result.getJSONObject("action").getString("cont").equals("knast")) {
			Output.printClockLn("In den Knast", Output.INFO);

			if (getKlatsche().useCardAsk(PlanBundesklatsche.CARD_KNASTRAUS)) {
				return true;
			}

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
			
			/*System.out.println("Geschenk!!!!");
			System.exit(1);*/
			Output.printClockLn("Knast: Freiwurf", Output.INFO);
			getKlatsche().rollAndOutputDice();
			Control.sleep(10);
			throw new NextField();
		}
	}

}
