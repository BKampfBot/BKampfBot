package bkampfbot.plan;

import json.JSONObject;
import bkampfbot.exception.FatalError;

public class PlanBoeseKrieg extends PlanBoese {
	private static OpponentList list;
	private static Opponent last;

	public PlanBoeseKrieg(JSONObject object) throws FatalError {
		super(object);

		if (PlanBoeseKrieg.list == null) {
			PlanBoeseKrieg.list = new OpponentList();
		}
	}

	@Override
	protected OpponentList getOpponentList() {
		return PlanBoeseKrieg.list;
	}

	public static void initiate() {
		PlanBoeseKrieg.list = null;
	}

	@Override
	protected final String getJsonObjectName() {
		return "BoeseKrieg";
	}

	@Override
	protected final String getEnemyListUri() {
		return "/1/war";
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
