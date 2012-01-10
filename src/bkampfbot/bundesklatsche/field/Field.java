package bkampfbot.bundesklatsche.field;

import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
import json.JSONException;
import json.JSONObject;

public abstract class Field {
	protected JSONObject result;

	public Field(JSONObject result) {
		this.result = result;
	}

	public static Field getField(int position, JSONObject result) {
		switch (position) {

		case 0:
			return new StartField(result);

		case 1:
		case 16:
		case 23:
		case 31:
			return new SchatzField(result);

		case 2:
			return new KampfField("Bayern", result);

		case 3:
		case 11:
		case 26:
		case 37:
			return new EreignisField(result);

		case 4:
			return new NordBahnhofField(result);

		case 5:
			return new KampfField("Baden-Württemberg", result);

		case 6:
		case 18:
		case 24:
		case 34:
			return new AktionField(result);

		case 7:
			return new KampfField("Hamburg", result);

		case 8:
		case 12:
		case 21:
		case 36:
			return new VierGewinntField(result);

		case 9:
			return new KampfField("Saarland", result);

		case 10:
			return new ZumKnastField(result);

		case 13:
			return new KampfField("Brandenburg", result);

		case 14:
			return new KampfField("Thüringen", result);

		case 15:
			return new OstBahnhofField(result);

		case 17:
			return new KampfField("Bremen", result);

		case 19:
			return new KampfField("Schleswig-Holstein", result);

		case 20:
			return new KnastField(result);

		case 22:
			return new KampfField("Hessen", result);

		case 25:
			return new KampfField("Rheinland-Pfalz", result);

		case 27:
			return new KampfField("Sachsen", result);

		case 28:
			return new KampfField("Nordrhein-Westfalen", result);

		case 29:
			return new KampfField(result, 3);

		case 30:
			return new HopTopField(result);

		case 32:
			return new KampfField("Sachsen-Anhalt", result);

		case 33:
			return new KampfField("Niedersachsen", result);

		case 35:
			return new KampfField("Mecklenburg-Vorpommern", result);

		case 38:
			return new KampfField("Berlin", result);

		case 39:
		default:
			return new KampfField(result, 10);
		}
	}

	public abstract boolean action() throws FatalError, RestartLater,
			JSONException;
}
