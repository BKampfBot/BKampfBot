package bkampfbot.plan;

import json.JSONObject;
import bkampfbot.exception.FatalError;

public class PlanBoeseBeute extends PlanBoese {
	private static OpponentList list;
	private static Opponent last;

	public PlanBoeseBeute(JSONObject object) throws FatalError {
		super(object);

		if (PlanBoeseBeute.list == null) {
			PlanBoeseBeute.list = new OpponentList();
		}
	}

	@Override
	protected OpponentList getOpponentList() {
		return PlanBoeseBeute.list;
	}
	
	public static void initiate() {
		PlanBoeseBeute.list = null;
	}
	
	@Override
	protected final String getJsonObjectName() {
		return "BoeseBeute";
	}

	@Override
	protected final String getEnemyListUri() {
		return "/1/prize";
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
