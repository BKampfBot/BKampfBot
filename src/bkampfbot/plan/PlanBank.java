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
import bkampfbot.output.Output;
import bkampfbot.utils.Bank;

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

	public PlanBank(int money) {
		this.setName("Bank");
		this.money = money;
	}

	public final void run() {
		Output.printClockLn("-> Bank", Output.INFO);

		Bank.putMoney(money);
	}

	@Override
	public boolean isPreRunningable() {
		return true;
	}
}
