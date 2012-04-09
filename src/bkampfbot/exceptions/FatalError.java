package bkampfbot.exceptions;

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
 * Sendet einen fatalen Fehler zum Controller.
 * 
 * Der Controller versucht danach, den kompletten Bot neu zu starten. Tritt
 * öfter ein fataler Fehler auf, wird das Programm beendet.
 */
public class FatalError extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 201102020008L;

	public FatalError(String message) {
		super(message);
	}
}
