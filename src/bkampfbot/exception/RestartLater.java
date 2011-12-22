package bkampfbot.exception;

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

/**
 * Starten den Bot später neu
 * 
 * Wird diese Exception geworfen, wird der Bot irgendwann automatisch neu
 * gestartet. Im Vergleich zum Neustart bei einem fatalen Fehler, wird hier kein
 * Versuch abgezogen.
 */
public class RestartLater extends Exception {
	private static final long serialVersionUID = 201102060227L;

	public RestartLater() {
		super();
	}

	public RestartLater(String message) {
		super(message);
	}
}
