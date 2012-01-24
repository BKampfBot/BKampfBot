package bkampfbot.modes;

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
import bkampfbot.Utils;
import bkampfbot.output.Output;
import json.JSONException;
import json.JSONObject;

public final class ScratchTicket {

	public ScratchTicket() {
		Output.printClockLn("Rubbellos", 1);

		try {
			for (int i = 0; i < 3; i++) {
				Utils.visit("kiosk/");
				Utils.visit("kiosk/losKaufen/");
				Utils.visit("kiosk/lose/");

				JSONObject result = Utils.getJSON("kiosk/getLos");
				String type = result.getString("type");
				if (!type.equals("kein Gewinn")) {
					Output.printTabLn("Gewinn: " + type + ": "
							+ result.getString("data"), 1);
				}
				type = result.getString("extratype");
				if (!type.equals("kein Gewinn")) {
					Output.printTabLn("Gewinn: " + type + ": "
							+ result.getString("extradata"), 1);
				}

				Control.sleep(15);

				Utils.visit("kiosk/getLosWin");
			}
		} catch (JSONException e) {
			Output
					.printTabLn(
							"Schon alle Lose für heute gekauft oder nicht genügend Punkte.",
							1);
		}
	}
}
