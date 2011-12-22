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
import bkampfbot.Utils;
import bkampfbot.exception.FatalError;
import bkampfbot.output.InfoFile;
import bkampfbot.output.Output;
import bkampfbot.state.User;

import json.JSONException;
import json.JSONObject;

/**
 * PlanGolden benötigt folgende Konfiguration: {"Golden":true}
 * 
 * @author georf
 * 
 */
public final class PlanGolden extends PlanObject {

	private int medicine = -1;

	public PlanGolden(JSONObject object) throws FatalError {
		this.setName("Golden");

		try {
			object.getBoolean("Golden");
		} catch (JSONException e) {
			
			try {
				JSONObject help = object.getJSONObject("Golden");
				
				
				try {
					this.medicine   = help.getInt("Medizin");
				} catch (JSONException r) {
				}
				
			} catch (JSONException t) {
				throw new FatalError("Config error: Golden");
			}
		}
	}

	public final void run() throws FatalError {
		Output.printClockLn("-> Golden", 1);

		if (!Utils.fightAvailable(20)) {
			return;
		}

		Utils.visit("challenge/");
		Utils.visit("sieben/intro/");
		Utils.visit("sieben/index/");

		try {
			JSONObject ob = Utils
					.getJSON("sieben/siebenData/");

			Control.sleep(5);

			Output.printTab("Kampf mit " + ob.getString("lastName") + " ("
					+ ob.getString("lastId") + ")", 1);

			Utils.visit("sieben/fight/"
					+ ob.getString("lastId"));

			ob = Utils.getJSON("sieben/fightData/");

			ob.remove("result");

			if (ob.getInt("won") == 1) {
				Output.println(" - won", 1);
			} else {
				Output.println(" - lost", 1);
			}

			InfoFile.writeGolden(ob);

			Control.sleep(5);

			Utils.visit("sieben/");

			ob = Utils.getJSON("sieben/siebenData/");

			if (medicine != -1) {
				
				// Aktualisieren
				Control.current.getCharacter();

				int procent = Math.round((User.getCurrentLivePoints()
						/ User.getMaxLivePoints()) * 100);

				// Wenn weniger Prozent Leben als angegen, dann Medizin kaufen.
				if (procent < medicine) {
					Utils.buyMedicine();
				}

				Utils.visit("sieben/");

				ob = Utils.getJSON("sieben/siebenData/");
			}
			long miliSeconds = ob.getInt("wait") * 1000 + 1000;
			int min = Math.round(miliSeconds / 60000);

			Control.sleep((min + 1) * 600, 2);

		} catch (JSONException e) {
			Output.printTabLn("Kein Kampf möglich", 1);

			if (Control.debug)
				e.printStackTrace();
		}
	}
}
