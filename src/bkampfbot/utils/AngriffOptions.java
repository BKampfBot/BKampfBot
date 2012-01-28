package bkampfbot.utils;

import json.JSONException;
import json.JSONObject;

public class AngriffOptions {

	/**
	 * Apotheke besuchen?
	 */
	private int medicine = -1;

	/**
	 * Zwerg kaufen?

	 * <pre>
	 * wert < 0           => nicht einlösen
	 * gewonnen >= wert   => einlösen
	 * </pre>
	 */
	private int buyCrystal = -1;

	public AngriffOptions(JSONObject options) {

		if (options.has("Zwerg")) {
			try {
				if (options.getBoolean("Zwerg")) {
					buyCrystal = 0;
				}
			} catch (JSONException r) {
				try {
					buyCrystal = options.getInt("Zwerg");
				} catch (JSONException p) {
				}
			}
		}

		if (options.has("Medizin")) {
			try {
				medicine = options.getInt("Medizin");
			} catch (JSONException r) {
			}
		}
	}

	public int getMedicine() {
		return medicine;
	}

	public int getBuyCrystal() {
		return buyCrystal;
	}
}
