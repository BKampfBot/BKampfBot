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
import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class HopTopField extends Field {

	public HopTopField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() throws JSONException {
		Output.printClockLn("HopTop-Feld", Output.INFO);
		Control.sleep(10);

		JSONObject set = Utils.getJSON("bundesklatsche/set_hoptop/"
				+ getResult().getJSONObject("char").getInt("beute"));

		int code = Math.round(((set.getInt("srn") * 5 + 234) * 2 - 1104) / 5F);

		Control.sleep(150, Output.DEBUG);

		try {
			Utils.getJSON("bundesklatsche/fin_hoptop/1/" + code);
			Control.sleep(10);
			return true;
		} catch (JSONException e) {
			Output.printTabLn("Es gab einen Fehler bei HopTop", Output.ERROR);
			Control.sleep(10);
			return false;
		}
	}
}
