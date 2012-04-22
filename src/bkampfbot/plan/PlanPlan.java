package bkampfbot.plan;

import bkampfbot.PlanManager;
import bkampfbot.exceptions.FatalError;
import bkampfbot.exceptions.RestartLater;
import json.JSONArray;
import json.JSONException;
import json.JSONObject;

public class PlanPlan extends PlanObject{
	private PlanManager manager;
	

	protected PlanPlan(JSONObject object, Object array, int length) throws JSONException, FatalError {
		super("Plan");
		
		if (array != null && array instanceof JSONArray) {
			JSONArray arr = (JSONArray) array;
			manager = new PlanManager(arr.length());
			
			for (int i = 0; i < arr.length(); i++) {
				manager.add(PlanObject.get(arr.getJSONObject(i)));
			}
			
			manager.setLoop(length);
			
		} else {
			configError();
		}
		
	}
	
	public void run() throws FatalError, RestartLater {
		manager.reset();
		
		manager.run();
	}

}
