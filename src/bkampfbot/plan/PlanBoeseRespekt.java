package bkampfbot.plan;

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

import json.JSONObject;
import bkampfbot.exceptions.FatalError;
import bkampfbot.state.Opponent;

public class PlanBoeseRespekt extends PlanBoese {
	private static OpponentList list;
	private static Opponent last;

	public PlanBoeseRespekt(JSONObject object) throws FatalError {
		super(object, "BoeseRespekt");

		if (PlanBoeseRespekt.list == null) {
			PlanBoeseRespekt.list = new OpponentList();
		}
	}

	@Override
	protected OpponentList getOpponentList() {
		return PlanBoeseRespekt.list;
	}

	public static void initiate() {
		PlanBoeseRespekt.list = null;
	}

	@Override
	protected final String getEnemyListUri() {
		return "/1/reputation";
	}

	@Override
	protected Opponent getOpponent() {
		return last;
	}

	@Override
	protected void setOpponent(Opponent l) {
		last = l;
	}

}
