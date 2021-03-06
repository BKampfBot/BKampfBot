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
import bkampfbot.plan.PlanObject;
import json.JSONException;
import json.JSONObject;

public final class ScratchTicket extends PlanObject{
	private int count = 3;

	public static ScratchTicket getInstance() {
		return new ScratchTicket(new JSONObject());
	}
	
	public ScratchTicket(int count) {
		super("Rubbellos");
		this.count = count;
	}
	
	public ScratchTicket(JSONObject setup) {
		super("Rubbellos");
	}
	
	public void run() {
		buy();
	}
		
	public int buy() {
		Output.printClockLn("Rubbellos", 1);
		
		int bought = 0;

		try {
			for (int i = 0; i < count; i++) {
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
				
				bought++;
			}
		} catch (JSONException e) {
			Output
					.printTabLn(
							"Schon alle Lose für heute gekauft oder nicht genügend Punkte.",
							1);
		}
		
		return bought;
	}
}
