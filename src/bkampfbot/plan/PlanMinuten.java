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

import json.JSONObject;
import bkampfbot.Control;
import bkampfbot.exceptions.ConfigError;
import bkampfbot.exceptions.FatalError;
import bkampfbot.output.Output;

/**
 * PlanMinuten benötigt folgende Konfiguration: {"Minuten":3}
 * 
 * @author georf
 * 
 */
public final class PlanMinuten extends PlanObject {
	private int count;

	public PlanMinuten(JSONObject object, Object min) throws FatalError {
		super("Minuten");

		if (min != null && min instanceof Integer) {
			count = (Integer) min;
		} else {
			throw new ConfigError("Minuten");
		}

		if (count < 0) {
			Output.println("Config: Minuten is set to 0.", 0);
			count = 0;
		}
	}

	public final void run() throws FatalError {
		printJump("(" + count + ")");

		// cut it into parts to safe session
		int c = count;
		while (c > 10) {
			// sleep for 10 min
			Control.sleep(6000, 2);
			c -= 10;
			Control.current.getCharacter();
		}
		Control.sleep(600 * c, 2);
	}
}
