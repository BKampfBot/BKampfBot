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
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.NextField;
import bkampfbot.exceptions.RestartLater;
import bkampfbot.plan.PlanBundesklatsche;

public abstract class Field {
	protected PlanBundesklatsche klatsche;

	public Field(PlanBundesklatsche klatsche) {
		this.klatsche = klatsche;
	}

	public static Field getField(int position, PlanBundesklatsche klatsche) {
		switch (position) {

		case 0:
			return new StartField(klatsche);

		case 1:
		case 16:
		case 23:
		case 31:
			return new SchatzField(klatsche);

		case 2:
			return new KampfField("Bayern", klatsche);

		case 3:
		case 11:
		case 26:
		case 37:
			return new EreignisField(klatsche);

		case 4:
		case 15:
			return new BahnhofField(klatsche);

		case 5:
			return new KampfField("Baden-Württemberg", klatsche);

		case 6:
		case 18:
		case 24:
		case 34:
			return new AktionField(klatsche);

		case 7:
			return new KampfField("Hamburg", klatsche);

		case 8:
		case 12:
		case 21:
		case 36:
			return new VierGewinntField(klatsche);

		case 9:
			return new KampfField("Saarland", klatsche);

		case 10:
		case 20:
			return new KnastField(klatsche);

		case 13:
			return new KampfField("Brandenburg", klatsche);

		case 14:
			return new KampfField("Thüringen", klatsche);

		case 17:
			return new KampfField("Bremen", klatsche);

		case 19:
			return new KampfField("Schleswig-Holstein", klatsche);

		case 22:
			return new KampfField("Hessen", klatsche);

		case 25:
			return new KampfField("Rheinland-Pfalz", klatsche);

		case 27:
			return new KampfField("Sachsen", klatsche);

		case 28:
			return new KampfField("Nordrhein-Westfalen", klatsche);

		case 29:
			return new KampfField(klatsche, 3, "E-Werk");

		case 30:
			return new HopTopField(klatsche);

		case 32:
			return new KampfField("Sachsen-Anhalt", klatsche);

		case 33:
			return new KampfField("Niedersachsen", klatsche);

		case 35:
			return new KampfField("Mecklenburg-Vorpommern", klatsche);

		case 38:
			return new KampfField("Berlin", klatsche);

		case 39:
		default:
			return new KampfField(klatsche, 10, "Kampfrausch");
		}
	}

	public JSONObject getResult() {
		return klatsche.getLastResult();
	}

	public JSONObject getConfig() {
		return klatsche.getConfig();
	}

	public PlanBundesklatsche getKlatsche() {
		return klatsche;
	}

	public abstract boolean action() throws FatalError, RestartLater,
			JSONException, NextField;
}
