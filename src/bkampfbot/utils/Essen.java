package bkampfbot.utils;

/*
 Copyright (C) 2012  georf@georf.de

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

		Output.print(" für " + minPunkte + ": ", Output.INFO);

		if (punkte >= minPunkte) {

			Output.println("Erfolgreich", Output.INFO);
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
