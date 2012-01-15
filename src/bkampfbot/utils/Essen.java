package bkampfbot.utils;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;

/**
 * 
 * @author georf
 * 
 *         <pre>
 * Essen.get().buy();
 * </pre>
 */
public class Essen {
	public Essen() {

	}

	public boolean buy() {
		String kiosk = Utils.getString("kiosk/");

		Output.printClock("Kaufe Tagesgericht", Output.INFO);
		
		Control.sleep(5);

		int i = kiosk.indexOf("fleispunkte");
		if (i == -1) {
			Output.println(": Fehler", Output.INFO);
			return false;
		}
		kiosk = kiosk.substring(i);

		int punkte = Integer.valueOf(kiosk.substring(0, kiosk.indexOf("\n"))
				.replaceAll("[^0-9]", ""));

		i = kiosk.indexOf("gerichtfortunes");
		if (i == -1) {
			Output.println(": Fehler", Output.INFO);
			return false;
		}
		kiosk = kiosk.substring(i);

		int minPunkte = Integer.valueOf(kiosk.substring(0, kiosk.indexOf("\n"))
				.replaceAll("[^0-9]", ""));

		Output.println("für " + minPunkte + ": ", Output.INFO);

		if (punkte >= minPunkte) {

			Output.print("Erfolgreich", Output.INFO);
			Utils.getString("kiosk/getGericht/");
			return true;

		}

		Output.println("Misslungen", Output.INFO);
		return false;
	}

	/**
	 * Factory
	 * 
	 * @return
	 */
	public static Essen get() {
		return new Essen();
	}
}
