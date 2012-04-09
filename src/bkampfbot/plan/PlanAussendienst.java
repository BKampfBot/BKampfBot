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
import bkampfbot.utils.Aussendienst;

/**
 * PlanAussendienst benötigt folgende Konfiguration: {"Aussendienst":0} oder
 * {"Aussendienst":{"Stufe":0,"Medizin":50}}
 * 
 * @author georf
 * 
 */

public final class PlanAussendienst extends PlanObject {
	private Aussendienst dienst;

	public PlanAussendienst(JSONObject help, Object obj) throws FatalError {
		super("Aussendienst");

		if (isInt(obj)) {

			dienst = new Aussendienst((Integer) obj);

		} else {

			try {
				int difficult = help.getInt("Stufe");
				int medicine = -1;

				try {
					medicine = help.getInt("Medizin");
				} catch (JSONException e3) {
				}

				dienst = new Aussendienst(difficult, medicine);

			} catch (JSONException e2) {
				configError();
			}
		}
	}
	
	@Override
	public void run() throws FatalError {
		dienst.run();
	}

	@Override
	public boolean isPreRunningable() {
		return true;
	}
}
