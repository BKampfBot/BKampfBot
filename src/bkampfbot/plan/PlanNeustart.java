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
import bkampfbot.output.Output;
import json.JSONObject;

/**
 * 
 * PlanStopp benötigt folgende Konfiguration: {"Neustart":true} oder {"Neustart":1}
 * 
 * @author georf
 *
 */
public final class PlanNeustart extends PlanObject {

	public PlanNeustart(JSONObject object) throws FatalError {
		this.setName("Neustart");
	}

	public final void run() throws RestartLater {
		Output.printClockLn("-> Neustart", 1);
		throw new RestartLater("Neustart");
	}

}
