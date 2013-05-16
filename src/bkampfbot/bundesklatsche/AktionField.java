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

import org.apache.commons.lang.math.RandomUtils;

import bkampfbot.Control;
import bkampfbot.exceptions.FatalError;
import bkampfbot.modes.Jagd;
import bkampfbot.modes.Pins;
import bkampfbot.modes.Quiz;
import bkampfbot.modes.ScratchTicket;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;
import bkampfbot.state.User;
import bkampfbot.utils.Aussendienst;
import bkampfbot.utils.Bank;
import bkampfbot.utils.Casino;
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
		 * "action": { "cont": "", "done": 0, "gold": 159, "text":
		 * "Der Schwimmkurs im hiesigen Hallenbad fordert deine ganze Kraft, steigere deinen Fitnesswert um  1 Punkt!"
		 * , "cancel": 1, "klatschen": 3 },
		 */
		String text = getResult().getJSONObject("action").getString("text");

		Output.printClockLn("Aktionsfeld", Output.INFO);
		Output.printTabLn(text, Output.DEBUG);

		if (text.equalsIgnoreCase("Der Schwimmkurs im hiesigen Hallenbad fordert deine ganze Kraft, steigere deinen Fitnesswert um  1 Punkt!")) {

			if (getKlatsche().useCardAsk(PlanBundesklatsche.CARD_PROTEIN)) {
				Output.printClockLn("Karte Protein gesetzt", Output.INFO);
				return cancelButton();
			} else if (!getConfig().isNull("Fitness")) {

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

		if (text.equalsIgnoreCase("Du bist im Jagdfieber, löse ein Wort bei der Wörterjagd!")) {

			try {
				if (!getConfig().getBoolean("Jagd")) {
					cancelButton();
				}
			} catch (JSONException e) {
			}

			Jagd jagd = Jagd.getInstance(1);
			jagd.run();
			return cancelButton(jagd.getWordsSolved() <= 0);
		}

		if (text.equalsIgnoreCase("Für ein anstehendes Referat musst du besonders konzentriert sein, steigere deinen Wahrnehmungswert um 1 Punkt!")) {

			if (!getConfig().isNull("Wahrnehmung")) {

				boolean bank = false;
				try {
					bank = getConfig().getBoolean("Wahrnehmung");
				} catch (JSONException e) {
				}

				int bought = Skill.get("Wahrnehmung").buy(1, bank);
				return cancelButton(bought <= 0);

			} else {
				return cancelButton();
			}
		}

		if (text.equalsIgnoreCase("Du bist heute gut drauf in deiner Außendiensttätigkeit, absolviere einen leichten AD und kassiere einen Sonderbonus!")) {

			boolean doit = true;
			try {
				if (!getConfig().getBoolean("Aussendienst")) {
					doit = false;
				}
			} catch (JSONException e) {
			}
			if (doit) {
				Aussendienst aussen = new Aussendienst(0);
				aussen.run();
			}
			return true;
		}

		if (text.equalsIgnoreCase("Die nächste Rate für deinen neuerworbenen Fernseher ist fällig, zahle 300 D-Mark auf dein Sparkassenkonto  ein um diese begleichen zu können!")) {

			return cancelButton(!Bank.putMoney(300));
		}

		if (text.equalsIgnoreCase("Du warst im Fitnesscenter und hast stundenlang deine Armmuskulatur trainiert, steigere deinen Skill Mukkies um 1 Punkt!")) {

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

		if (text.equalsIgnoreCase("Man wirft dir vor berechenbar zu sein, speichere eine neue Kampfstrategie ab!")) {
			Strategie.getRandom().save();
			return true;
		}

		if (text.equalsIgnoreCase("Du warst heute den ganzen Tag auf den Beinen und hast irrsinnigen Hunger, besuche Bogdan und kauf dir eins seiner Gourmet-Essen!")) {

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

		if (text.equalsIgnoreCase("Der alljährliche Schützenwettbewerb im Kastanienweitwurf steht an, steigere deinen Skill Schleuderkraft um 1 Punkt!")) {

			if (!getConfig().isNull("Schleuderkraft")) {

				boolean bank = false;
				try {
					bank = getConfig().getBoolean("Schleuderkraft");
				} catch (JSONException e) {
				}

				int bought = Skill.get("Schleuderkraft").buy(1, bank);
				return cancelButton(bought <= 0);

			} else {
				return cancelButton();
			}
		}

		if (text.equalsIgnoreCase("Heute steht alles im Zeichen deines persönlichen Glücks, steigere deinen Skill Glück um 1 Punkt!")) {

			if (!getConfig().isNull("Glueck")) {

				boolean bank = false;
				try {
					bank = getConfig().getBoolean("Glueck");
				} catch (JSONException e) {
				}

				int bought = Skill.get("Glueck").buy(1, bank);
				return cancelButton(bought <= 0);

			} else {
				return cancelButton();
			}
		}

		if (text.equalsIgnoreCase("Wer nicht wagt, der nicht gewinnt! Gehe ins Casino und drehe dreimal am einarmigen Banditen!")) {
			if (User.getLevel() < 10) {
				Output.printTabLn("Du darfst noch nicht ins Casino.",
						Output.INFO);
				return cancelButton();
			} else {
				try {
					if (!getConfig().getBoolean("Casino")) {
						return cancelButton();
					}
				} catch (JSONException e) {
				}

				Control.sleep(15);

				Casino bandit = new Casino(Casino.M100);

				Output.printTab("Drehe: ", Output.INFO);
				for (int i = 0; i < 3; i++) {
					if (bandit.run()) {
						Output.print(bandit.getMoney() + " DM  ", Output.INFO);
					} else {
						Output.println("Fehler", Output.INFO);
						return cancelButton();
					}
					Control.sleep(15);
				}
				Output.println("", Output.INFO);
				return true;
			}
		}

		if (text.equalsIgnoreCase("Deine kleine Zwergennichte hat Geburtstag, hebe 500 D-Mark von deinem Sparkassenkonto ab, um ihr etwas Schönes kaufen zu können!")) {

			if (getConfig().has("Einzahlen")) {
				try {
					if (!getConfig().getBoolean("Einzahlen")) {
						return cancelButton();
					}
				} catch (JSONException e) {
				}
			}
			return cancelButton(!Bank.getMoney(500));
		}

		if (text.equalsIgnoreCase("Du bist in famoser Kauflaune heute, erwerbe einen Pin im Shop!")) {

			String[] pins = { "11", "21", "31", "41" };
			String pinToBuy = pins[RandomUtils.nextInt(pins.length)];

			try {
				if (!getConfig().getBoolean("Pin")) {
					return cancelButton();
				}
			} catch (JSONException e) {

				try {
					pinToBuy = getConfig().getString("Pin");
				} catch (JSONException f) {
				}
			}

			Pins p = new Pins(pinToBuy);

			return cancelButton(p.buy() <= 0);
		}

		if (text.equalsIgnoreCase("Kaufe ein Rubbellos bei Bogdan am Kiosk, vielleicht gelingt dir heute der große Wurf!")) {

			try {
				if (!getConfig().getBoolean("Los")) {
					return cancelButton();
				}
			} catch (JSONException e) {
			}

			ScratchTicket st = new ScratchTicket(1);
			return cancelButton(st.buy() <= 0);
		}

		if (text.equalsIgnoreCase("Du fühlst dich geistig superfit heute! Löse das Tagesquiz mit mindestens 5 richtigen Antworten!")) {
			try {
				if (!getConfig().getBoolean("Quiz")) {
					return cancelButton();
				}
			} catch (JSONException e) {
			}

			Quiz q = new Quiz(2);
			return cancelButton(q.runQuiz() < 5);
		}

		/**
		 * Noch zu implementieren: Wer nicht wagt, der nicht gewinnt! Gehe ins
		 * Casino und drehe dreimal am einarmigen Banditen!
		 * 
		 */

		// TODO Nicht implementiert

		Output.printTabLn("Nicht implementiert: "
				+ this.getClass().getSimpleName(), Output.ERROR);
		Output.println(text, Output.INFO);
		return false;
	}

	/**
	 * Wenn press true ist, wird auf Abbrechen gedrückt.
	 * 
	 * @param press
	 * @return
	 * @throws JSONException
	 */
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
