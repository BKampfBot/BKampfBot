package bkampfbot.exceptions;

import bkampfbot.state.Opponent;

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
 * Gibt an, dass der übergebene Feind zur Zeit nicht angreifbar ist.
 */
public final class BadOpponent extends Exception {
	private static final long serialVersionUID = 201102030046L;
	private final Opponent opponent;

	public BadOpponent(Opponent opp) {
		this.opponent = opp;
	}

	public final Opponent getOpponent() {
		return opponent;
	}
}
