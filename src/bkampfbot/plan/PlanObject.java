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
import bkampfbot.exceptions.ConfigError;
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

public abstract class PlanObject {
	private String name;
	protected PlanManager planManager;

	public static PlanObject get(JSONObject object) throws JSONException, FatalError {
		String[] keys = JSONObject.getNames(object);
		if (keys == null || keys.length != 1) {
			throw new ConfigError("Fehler in der Struktur");
		}

		JSONObject setup = new JSONObject();
		Object obj = null;
		String lower = keys[0].toLowerCase();
		
		try {
			setup = object.getJSONObject(keys[0]);
		} catch (JSONException e) {
			obj = object.get(keys[0]);
		}

		if (lower.equals("angriff")) {

			return new PlanAngriff(setup, "PlanManager");

		} else if (lower.equals("aussendienst")) {
			return new PlanAussendienst(setup, obj);

		} else if (lower.equals("bank")) {
			return new PlanBank(setup, obj);

		} else if (lower.equals("boesebeute")) {
			return new PlanBoeseBeute(setup);

		} else if (lower.equals("boesekrieg")) {
			return new PlanBoeseKrieg(setup);

		} else if (lower.equals("boeserespekt")) {
			return new PlanBoeseRespekt(setup);

		} else if (lower.equals("minuten")) {
			return new PlanMinuten(setup, obj);

		} else if (lower.equals("stopp")) {
			return new PlanStopp(setup);

		} else if (lower.equals("neustart")) {
			return new PlanNeustart(setup);

		} else if (lower.equals("arbeiten")) {
			return new PlanArbeiten(setup, obj);

		} else if (lower.equals("kampf")) {
			return new PlanKampf(setup, obj);

		} else if (lower.equals("beschreibung")) {
			return new PlanBeschreibung(setup, obj);

		} else if (lower.equals("befehl")) {
			return new PlanBefehl(setup);

		} else if (lower.equals("golden")) {
			return new PlanGolden(setup);

		} else if (lower.equals("booster")) {
			return new PlanBooster(setup, obj);

		} else if (lower.equals("skill")) {
			return new PlanSkill(setup);

		} else if (lower.equals("bundesklatsche")) {
			return new PlanBundesklatsche(setup);

			// Es folgen die Modi
		} else if (lower.equals("gluecksrad")) {
			return new Gluecksrad(setup);

		} else if (lower.equals("jagd")) {
			return new Jagd(setup);

		} else if (lower.equals("lotto")) {
			return new Lottery(setup);

		} else if (lower.equals("pins")) {
			return new Pins(setup);

		} else if (lower.equals("tagesquiz")) {
			return new Quiz(setup);

		} else if (lower.equals("rubbellos")) {
			return new ScratchTicket(setup);

		} else if (lower.equals("tagesspiel")) {
			return new Tagesspiel(setup);

		} else if (lower.equals("weinkeller")) {
			return new Wein(setup);
		}

		throw new ConfigError("Du benutzt ein Planelement, welches nicht definiert wurde.");
	}

	protected PlanObject(String name) {
		this.name = name;
	}

	protected final void setName(String name) {
		this.name = name;
	}

	protected void printJump(String add) {
		Output.printClockLn("-> " + name + " " + add, Output.INFO);
	}

	protected void printJump() {
		Output.printClockLn("-> " + name, Output.INFO);
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
			Output.printClockLn("Springe zurück -> " + name, Output.INFO);
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
	
	public String getName() {
		return name;
	}
	
	protected void configError() throws ConfigError {
		throw new ConfigError(getName());
	}
	
	protected boolean isInt(Object obj) {
		return (obj != null && obj instanceof Integer);
	}
	
	protected boolean isStr(Object obj) {
		return (obj != null && obj instanceof String);
	}
}
