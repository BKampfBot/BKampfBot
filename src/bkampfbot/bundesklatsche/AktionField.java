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
import bkampfbot.exception.FatalError;
import bkampfbot.modes.Jagd;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanAussendienst;
import bkampfbot.plan.PlanBundesklatsche;
import bkampfbot.utils.Bank;
import bkampfbot.utils.Essen;
import bkampfbot.utils.Skill;
import bkampfbot.utils.Strategie;

public class AktionField extends Field {

	public AktionField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException, FatalError {
		Control.sleep(10);

		/**
		 * "action": { "cont": "", "done": 0, "gold": 159, "text":"Der Schwimmkurs im hiesigen Hallenbad fordert deine ganze Kraft, steigere deinen Fitnesswert um  1 Punkt!"
		 * , "cancel": 1, "klatschen": 3 },
		 */
		String text = getResult().getJSONObject("action").getString("text");

		Output.printClockLn("Aktionsfeld", Output.INFO);
		Output.printTabLn(text, Output.DEBUG);

		if (text
				.equalsIgnoreCase("Der Schwimmkurs im hiesigen Hallenbad fordert deine ganze Kraft, steigere deinen Fitnesswert um  1 Punkt!")) {

			if (!getConfig().isNull("Fitness")) {

				boolean bank = false;
				try {
					bank = getConfig().getBoolean("Fitness");
				} catch (JSONException e) {
				}

				int bought = Skill.get("Fitness").buy(1, bank);
				return cancelButton(bought <= 0);

			} else {
				return cancelButton();
			}
		}

		if (text
				.equalsIgnoreCase("Du bist im Jagdfieber, löse ein Wort bei der Wörterjagd!")) {
			Jagd jagd = new Jagd(1);
			jagd.run();
			return cancelButton(jagd.getWordsSolved() <= 0);
		}

		if (text
				.equalsIgnoreCase("Du bist heute gut drauf in deiner Außendiensttätigkeit, absolviere einen leichten AD und kassiere einen Sonderbonus!")) {
			JSONObject config = new JSONObject("{\"Aussendienst\":0}");
			PlanAussendienst aussen = new PlanAussendienst(config);
			aussen.run();
			return true;
		}

		if (text
				.equalsIgnoreCase("Die nächste Rate für deinen neuerworbenen Fernseher ist fällig, zahle 300 D-Mark auf dein Sparkassenkonto  ein um diese begleichen zu können!")) {

			return cancelButton(!Bank.putMoney(300));
		}

		if (text
				.equalsIgnoreCase("Du warst im Fitnesscenter und hast stundenlang deine Armmuskulatur trainiert, steigere deinen Skill Mukkies um 1 Punkt!")) {

			if (!getConfig().isNull("Mukkies")) {

				boolean bank = false;
				try {
					bank = getConfig().getBoolean("Mukkies");
				} catch (JSONException e) {
				}

				int bought = Skill.get("Mukkies").buy(1, bank);
				return cancelButton(bought <= 0);

			} else {
				return cancelButton();
			}
		}

		if (text
				.equalsIgnoreCase("Man wirft dir vor berechenbar zu sein, speichere eine neue Kampfstrategie ab!")) {
			Strategie.getRandom().save();
			return true;
		}

		if (text
				.equalsIgnoreCase("Du warst heute den ganzen Tag auf den Beinen und hast irrsinnigen Hunger, besuche Bogdan und kauf dir eins seiner Gourmet-Essen!")) {

			boolean essen = true;
			try {
				essen = getConfig().getBoolean("Essen");
			} catch (JSONException e) {
			}
			if (essen) {
				return cancelButton(!Essen.get().buy());
			} else {
				return cancelButton();
			}
		}

		/**
		 * Noch zu implementieren:
		 * 
		 * 
		 */

		// TODO Nicht implementiert

		Output.printTabLn("Nicht implementiert: "
				+ this.getClass().getSimpleName(), Output.ERROR);
		Output.println(text, Output.INFO);
		return false;
	}

	private boolean cancelButton(boolean press) throws JSONException {
		if (press) {
			Control.sleep(10);
			Output.printTabLn("Abbrechen", Output.DEBUG);
			getKlatsche().rollTheDice();
			Control.sleep(10);
		}
		return true;
	}

	private boolean cancelButton() throws JSONException {
		return cancelButton(true);
	}

}
