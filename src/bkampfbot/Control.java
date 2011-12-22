package bkampfbot;

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

import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.state.Config;
import bkampfbot.state.Prevention;

public class Control {
	public final static String version = "1.2.9";
	public final static boolean debug = false;

	public static Instance current = null;

	public final static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			try {
				Control.current = new Instance(args);
				current.run();
			} catch (FatalError e) {
				Output.println(e.getMessage() + "\n", 0);
				System.exit(1);
			} catch (RestartLater e) {
				if (e.getMessage() == null || e.getMessage().equals("")) {
					i--;
					Output
							.println(
									"Es ist ein Fehler aufgetreten, der nicht zugeordnet werden konnte.\n"
											+ "Versuchen Sie den Fehler direkt über die Webseite zu finden. "
											+ "Vermutlich hängt der Spieler irgendwo fest, wann der Bot nicht erkennen kann. "
											+ "Oftmals scheitert es, wenn man manuell in den Ablauf eingegriffen hat oder wenn "
											+ "Account beim Starten nicht im Ausgangszustand ist.\n"
											+ "Der Bot wird sich in 3 min neu starten.",
									0);

					if (Control.debug) {
						e.printStackTrace();
					}

					Control.sleep(3 * 600);
				} else {
					Output.println("Geplanter Neustart: " + e.getMessage(), 2);
					Control.current.logout();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Legt den Bot schlafen
	 * 
	 * @param deciSec
	 *            Dezisekunden
	 */
	public final static void sleep(int deciSec) {
		sleep(deciSec, -1);
	}

	/**
	 * Legt den Bot schlafen und gibt eine Meldung aus
	 * 
	 * @param deciSec
	 *            Dezisekunden
	 * @param level
	 *            Ausgabelevel
	 */
	public final static void sleep(int deciSec, int level) {

		if (deciSec <= 0) {
			return;
		}

		// add time from config
		if (deciSec < 500) {
			double factor = Config.getSleepFactor();
			if (factor != 1) {
				if (deciSec > 60) {
					deciSec *= factor / 10;
				} else {
					deciSec *= factor;
				}
			}
		}

		if (level != -1) {
			Output.sleep(deciSec, level);
		}

		if (Config.getPrevention() && deciSec > 3 * 600) {
			deciSec = Prevention.getInstance().doSomething(deciSec);
		}

		quietSleep(deciSec * 100);
	}

	public final static void quietSleep(int milliSeconds) {

		if (milliSeconds <= 0) {
			return;
		}

		if (Control.debug) {
			int min = Math.round(milliSeconds / 60000);
			int sec = Math.round((milliSeconds / 1000) % 60);
			Output.println("Sleep for " + min + ":" + sec + " (" + milliSeconds
					+ ")", Output.DEBUG);
		}

		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Ausloggen und dann Programm beenden
	 */
	public final static void safeExit() {
		// logout
		Control.current.logout();

		// exit with default status
		System.exit(0);
	}

}
