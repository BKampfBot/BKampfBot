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

import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
import bkampfbot.output.InfoFile;
import bkampfbot.output.Output;

import json.JSONException;
import json.JSONObject;

/**
 * PlanBefehl benötigt folgende Konfiguration: {"Befehl":true} o
 * 
 * @author georf
 * 
 */

public final class PlanBefehl extends PlanObject {
	public PlanBefehl(JSONObject object) throws FatalError {
		this.setName("Befehl");

		try {
			object.getBoolean("Befehl");

		} catch (JSONException e) {
			throw new FatalError("Config error: Befehl have to be true");
		}
	}

	public final void run() throws FatalError, RestartLater, JSONException {
		Output.printClock("-> Befehl", 1);

		JSONObject now = InfoFile.getBefehl();
		while (now != null) {
			Output.println(" (get one)", 1);
			PlanObject doit = new PlanObject();
			doit = PlanObject.get(now);
			doit.run();
			now = InfoFile.getBefehl();
			Output.printClock("-> Befehl", 1);
		}
		Output.println(" (nothing)", 1);
	}
}
