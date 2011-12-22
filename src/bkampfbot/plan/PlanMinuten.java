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

import bkampfbot.Control;
import bkampfbot.exception.FatalError;
import bkampfbot.output.Output;

import json.JSONException;
import json.JSONObject;

/**
 * PlanMinuten benötigt folgende Konfiguration: {"Minuten":3}
 * 
 * @author georf
 * 
 */
public final class PlanMinuten extends PlanObject {
	private int count;

	public PlanMinuten(JSONObject object) throws FatalError {
		this.setName("Minuten");

		try {
			this.count = object.getInt("Minuten");
		} catch (JSONException e) {
			throw new FatalError("Config error: Minuten have to be an integer");
		}

		if (this.count < 0) {
			Output.println("Config: Minuten is set to 0.", 0);
			this.count = 0;
		}
	}

	public final void run() throws FatalError {
		Output.printClockLn("-> Minuten (" + this.count + ")", 1);

		// cut it into parts to safe session
		int count = this.count;
		while (count > 10) {
			// sleep for 10 min
			Control.sleep(6000, 2);
			count -= 10;
			Control.current.getCharacter();
		}
		Control.sleep(600 * count, 2);
	}
}
