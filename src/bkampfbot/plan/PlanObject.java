package bkampfbot.plan;

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

import java.util.Calendar;
import java.util.GregorianCalendar;

import bkampfbot.PlanManager;
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.modes.Gluecksrad;
import bkampfbot.modes.Jagd;
import bkampfbot.modes.Lottery;
import bkampfbot.modes.Pins;
import bkampfbot.modes.Quiz;
import bkampfbot.modes.ScratchTicket;
import bkampfbot.modes.Tagesspiel;
import bkampfbot.modes.Wein;
import bkampfbot.output.Output;
import bkampfbot.state.Config;

import json.JSONException;
import json.JSONObject;

public class PlanObject {
	private String name;
	protected PlanManager planManager;

	public static PlanObject get(JSONObject object) throws FatalError {
		String[] keys = JSONObject.getNames(object);
		if (keys == null || keys.length != 1) {
			throw new FatalError(
					"Konfiguration enthält Fehler in der Struktur.");
		}

		if (keys[0].equalsIgnoreCase("Angriff")) {
			
			return new PlanAngriff(object, "PlanManager");
			
		} else if (keys[0].equalsIgnoreCase("Aussendienst")) {
			return new PlanAussendienst(object);
			
		} else if (keys[0].equalsIgnoreCase("Bank")) {
			return new PlanBank(object);
			
		} else if (keys[0].equalsIgnoreCase("BoeseBeute")) {
			return new PlanBoeseBeute(object);
			
		} else if (keys[0].equalsIgnoreCase("BoeseKrieg")) {
			return new PlanBoeseKrieg(object);
			
		} else if (keys[0].equalsIgnoreCase("BoeseRespekt")) {
			return new PlanBoeseRespekt(object);
			
		} else if (keys[0].equalsIgnoreCase("Minuten")) {
			return new PlanMinuten(object);
			
		} else if (keys[0].equalsIgnoreCase("Stopp")) {
			return new PlanStopp(object);
			
		} else if (keys[0].equalsIgnoreCase("Neustart")) {
			return new PlanNeustart(object);
			
		} else if (keys[0].equalsIgnoreCase("Arbeiten")) {
			return new PlanArbeiten(object);
			
		} else if (keys[0].equalsIgnoreCase("Kampf")) {
			return new PlanKampf(object);
			
		} else if (keys[0].equalsIgnoreCase("Beschreibung")) {
			return new PlanBeschreibung(object);
			
		} else if (keys[0].equalsIgnoreCase("Befehl")) {
			return new PlanBefehl(object);
			
		} else if (keys[0].equalsIgnoreCase("Golden")) {
			return new PlanGolden(object);
			
		} else if (keys[0].equalsIgnoreCase("Booster")) {
			return new PlanBooster(object);
			
		} else if (keys[0].equalsIgnoreCase("Skill")) {
			return new PlanSkill(object);
			
		} else if (keys[0].equalsIgnoreCase("Bundesklatsche")) {
			return new PlanBundesklatsche(object);
			
			
			// Es folgen die Modi
		} else if (keys[0].equalsIgnoreCase("Gluecksrad")) {
			return new Gluecksrad(object);
			
		} else if (keys[0].equalsIgnoreCase("Jagd")) {
			return new Jagd(object);
			
		} else if (keys[0].equalsIgnoreCase("Lotto")) {
			return new Lottery(object);
			
		} else if (keys[0].equalsIgnoreCase("Pins")) {
			return new Pins(object);
			
		} else if (keys[0].equalsIgnoreCase("Tagesquiz")) {
			return new Quiz(object);
			
		} else if (keys[0].equalsIgnoreCase("Rubbellos")) {
			return new ScratchTicket(object);
			
		} else if (keys[0].equalsIgnoreCase("Tagesspiel")) {
			return new Tagesspiel(object);
			
		} else if (keys[0].equalsIgnoreCase("Weinkeller")) {
			return new Wein(object);
		}

		throw new FatalError("Die Konfiguration ist nicht korrekt. "
				+ "Du benutzt ein Planelement, welches nicht definiert wurde.");
	}

	protected final void setName(String name) {
		this.name = name;
	}

	public final String getName() {
		return this.name;
	}

	public void run() throws FatalError, JSONException, RestartLater {
		Output.printClockLn(this.getClass().getName()
				+ " does not implement run()", 0);
	}

	/**
	 * Ruft die nächsten Pläne vom Planmanger auf, falls die es unterstützen.
	 * 
	 * @param deciSeconds
	 *            Dezimalsekunden die Zeit sind
	 * @return restliche Zeit
	 * @throws RestartLater
	 * @throws FatalError
	 */
	public final int runNextPlan(int deciSeconds) throws FatalError,
			RestartLater {

		if (planManager == null) {
			return deciSeconds;
		}

		GregorianCalendar finish = new GregorianCalendar();
		finish.setTime(Config.getDate());
		finish.add(Calendar.MILLISECOND, deciSeconds * 100);

		while (((finish.getTimeInMillis() - new GregorianCalendar()
				.getTimeInMillis()) / 100) > 0
				&& planManager.runLookAhead())
			;

		if (planManager.wasLookAhead()) {
			Output.printClockLn("Springe zurück -> " + getName(), Output.INFO);
		}

		return (int) ((finish.getTimeInMillis() - new GregorianCalendar()
				.getTimeInMillis()) / 100);
	}

	public final void setPlanManager(PlanManager pM) {
		planManager = pM;
	}

	public boolean isPreRunningable() {
		return false;
	}
}
