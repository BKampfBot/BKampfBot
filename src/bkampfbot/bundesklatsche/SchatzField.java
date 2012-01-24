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
import bkampfbot.Control;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

/**
 * Geschenke satt. Auf dem Schatzfeld gibt es DM und Klatschen für lau. Ihr müßt
 * nichts dafür tun.
 * 
 * @author georf
 * 
 */
public class SchatzField extends Field {

	public SchatzField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {

		Output.printClockLn("Schatz: "
				+ getResult().getJSONObject("char").getString("num1")
				+ " Mark, "
				+ getResult().getJSONObject("char").getString("num2")
				+ " Klatschen", Output.INFO);
		Control.sleep(10);
		return true;
	}

}
