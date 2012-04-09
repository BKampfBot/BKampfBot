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

import json.JSONException;
import json.JSONObject;
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.output.BefehlFile;

/**
 * PlanBefehl benötigt folgende Konfiguration: {"Befehl":true} o
 * 
 * @author georf
 * 
 */

public final class PlanBefehl extends PlanObject {
	public PlanBefehl(JSONObject object) throws FatalError {
		super("Befehl");
	}

	public final void run() throws FatalError, RestartLater, JSONException {

		BefehlFile file = new BefehlFile();

		JSONObject now = file.getBefehl();
		while (now != null) {
			printJump("(hole einen)");
			PlanObject doit = PlanObject.get(now);
			doit.run();
			now = file.getBefehl();
		}
		printJump("(nichts)");
	}
}
