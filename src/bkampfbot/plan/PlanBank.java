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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.exception.FatalError;
import bkampfbot.output.Output;

import json.JSONException;
import json.JSONObject;

/**
 * PlanBank benötigt folgende Konfiguration: {"Bank":23} -> Es werden 23 Mark
 * auf die Bank gebracht, oder {"Bank":0} -> Es wird das Maximum auf die Bank
 * gebracht.
 * 
 * @author georf
 * 
 */
public final class PlanBank extends PlanObject {
	private int money;

	public PlanBank(JSONObject object) throws FatalError {
		this.setName("Bank");

		try {
			this.money = object.getInt("Bank");
		} catch (JSONException e) {
			throw new FatalError("Config error: Bank have to be an integer");
		}

		if (this.money < 0) {
			Output.println("Config: Bank is set to 0.", 0);
			this.money = 0;
		}
	}

	public final void run() {
		Output.printClockLn("-> Bank", 1);

		Utils.getString("city/index");
		Control.quietSleep(500);
		Utils.getString("sparkasse/banker");
		Control.quietSleep(500);
		String einzahlen = Utils.getString("sparkasse/einzahlen");
		Control.quietSleep(500);

		if (this.money == 0) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("max", "1"));
			nvps.add(new BasicNameValuePair("x", "44"));
			nvps.add(new BasicNameValuePair("y", "24"));
			Utils.getString("sparkasse/einzahlen", nvps);
		} else {

			if (einzahlen.indexOf(" D-Mark haben um etwas einzuzahlen.") != -1) {
				Output.printTabLn("Nicht genug D-Mark verhanden.", 2);
				return;
			}

			int index = einzahlen.indexOf("Du besitzt ");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return;
			}
			einzahlen = einzahlen.substring(index);

			index = einzahlen.indexOf(" D-Mark.");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return;
			}
			int moneyNow = Integer.valueOf(einzahlen.substring(0, index)
					.replaceAll("[^0-9]+", ""));

			index = einzahlen.indexOf("Bis auf ein Haushaltsgeld von ");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return;
			}
			einzahlen = einzahlen.substring(index);

			index = einzahlen.indexOf(" D-Mark kannst du dein Geld");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return;
			}
			int moneyMin = Integer.valueOf(einzahlen.substring(0, index)
					.replaceAll("[^0-9]+", ""));

			int moneyMax = moneyNow - moneyMin;

			if (this.money < moneyMax) {
				moneyMax = this.money;
			}

			// Post zusammenbauen
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("data[Bank][in]", String
					.valueOf(moneyMax)));
			nvps.add(new BasicNameValuePair("x", "110"));
			nvps.add(new BasicNameValuePair("y", "16"));

			Utils.getString("sparkasse/einzahlen", nvps);

			Output
					.printTabLn("Bringe " + moneyMax + " D-Mark auf die Bank.",
							2);

		}
	}

	@Override
	public boolean isPreRunningable() {
		return true;
	}
}
