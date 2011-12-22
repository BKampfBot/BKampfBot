package bkampfbot.plan;

import json.JSONObject;
import bkampfbot.exception.FatalError;

public class PlanBoeseRespekt extends PlanBoese {
	private static OpponentList list;
	private static Opponent last;

	public PlanBoeseRespekt(JSONObject object) throws FatalError {
		super(object);

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
	protected final String getJsonObjectName() {
		return "BoeseRespekt";
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
