package bkampfbot.utils;

import json.JSONException;
import json.JSONObject;
import bkampfbot.Utils;

public class Casino {
	public final static short M100 = 1;
	public final static short M250 = 2;
	public final static short M500 = 3;

	private final int modus;
	private int wonMoney = 0;

	public Casino(short modus) {
		this.modus = modus;
	}

	public boolean run() {
		Utils.getString("city/index");
		Utils.getString("casino/slotmachine");

		try {
			JSONObject data = Utils.getJSON("casino/slotmachineData/0");
			// {"jackpot":"192028","fortunes":"990","error":"ok","freispiele":0,"spiele":100,"bet":0}

			int min = 100;
			if (modus == 2) {
				min = 250;
			} else if (modus == 3) {
				min = 500;
			}

			if (min > Integer.valueOf(data.getString("fortunes"))) {
				return false;
			}

			data = Utils.getJSON("casino/slotmachineData/" + modus);
			// {"jackpot":192079,"fortunes":890,"oldFortunes":890,"gewinn":0,"gfortunes":0,"error":"ok","freispiele":0,"spiele":99}

			wonMoney = data.getInt("gewinn");
			return true;

		} catch (JSONException e) {
			return false;
		}

	}

	public int getMoney() {
		return wonMoney;
	}
}
