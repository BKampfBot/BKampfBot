package bkampfbot.utils;

import bkampfbot.Utils;

public class Essen {
	public Essen() {

	}
	
	public boolean buy() {
		String kiosk = Utils.getString("kiosk/");
		
		int i = kiosk.indexOf("fleispunkte");
		if (i == -1) {
			return false;
		}
		kiosk = kiosk.substring(i);
		
		int punkte = Integer.valueOf(kiosk.substring(0, kiosk.indexOf("\n")).replaceAll("[^0-9]", ""));

		i = kiosk.indexOf("gerichtfortunes");
		if (i == -1) {
			return false;
		}
		kiosk = kiosk.substring(i);
		
		int minPunkte = Integer.valueOf(kiosk.substring(0, kiosk.indexOf("\n")).replaceAll("[^0-9]", ""));
		
		if (punkte >= minPunkte) {
			Utils.getString("kiosk/getGericht/");
			return true;
		}
		
		return false;
	}
	
	public static Essen get() {
		return new Essen();
	}
}
