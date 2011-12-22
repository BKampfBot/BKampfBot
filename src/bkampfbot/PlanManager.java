package bkampfbot;

import java.util.Calendar;
import java.util.GregorianCalendar;

import json.JSONException;
import bkampfbot.exception.FatalError;
import bkampfbot.exception.RestartLater;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanObject;
import bkampfbot.state.Config;

public class PlanManager {

	private PlanObject[] plans;
	private int insertPointer = 0;
	private boolean aussendienst = false;
	private boolean infinity = true;

	private int runningPlan = -1;
	private int lookAheadPlan = -1;

	public PlanManager(int length) {
		plans = new PlanObject[length];
	}

	public void add(PlanObject plan) {
		if (insertPointer == plans.length) {
			PlanObject[] help = plans;
			plans = new PlanObject[insertPointer + 1];
			for (int i = 0; i < help.length; i++) {
				plans[i] = help[i];
			}
		}

		plan.setPlanManager(this);

		plans[insertPointer] = plan;
		insertPointer++;
	}

	public void setAussendienst() {
		aussendienst = true;
	}

	public void setUninfinity() {
		infinity = false;
	}

	public final void run() throws FatalError, RestartLater {

		runningPlan = -1;
		Calendar lastCall = null;

		do {
			if (plans.length == 0) {
				return;
			}
			Calendar now = new GregorianCalendar();
			now.setTime(Config.getDate());
			now.add(Calendar.MINUTE, -2);

			if (lastCall != null && lastCall.after(now)) {
				Output
						.println(
								"Der Bot war bei der Abarbeitung der Pläne zu schnell. "
										+ "Vermutlich trat ein Fehler auf. Damit wir nicht auffallen, "
										+ "warten wir 5 Minuten.", 1);

				Control.sleep(3000);

			}

			if (aussendienst && !Utils.serviceAvalible()) {
				return;
			}

			lastCall = new GregorianCalendar();
			lastCall.setTime(Config.getDate());

			/*
			 * Wir gehen nur aus diesem Plan raus, wenn in der Zeit ein neuer
			 * Tag anbricht.
			 */
			GregorianCalendar beforePlan = new GregorianCalendar();
			beforePlan.setTime(Config.getDate());

			for (int i = 0; i < plans.length
					&& (!aussendienst || Utils.serviceAvalible()); i++) {

				if (lookAheadPlan != -1) {
					i = getNextPlan(lookAheadPlan);
				}
				
				if (plans[i] == null)
					continue;

				runningPlan = i;
				lookAheadPlan = -1;

				reliableExecution(plans[i]);
			}

			Control.sleep(5);

			Control.current.getCharacter();

			Control.sleep(5);

			Calendar today = new GregorianCalendar();
			today.setTime(Config.getDate());
			today.set(Calendar.HOUR, 0);
			today.set(Calendar.MINUTE, 0);

			if (!aussendienst && beforePlan.before(today)) {
				// Während des Abarbeitens wurde ein neuer Tag angebrochen
				return;
			}

		} while (infinity);
	}

	public final boolean runLookAhead() throws FatalError, RestartLater {
		int toRun;

		if (lookAheadPlan == -1) {
			
			// ersten LookAhead
			toRun = getNextPlan(runningPlan);

		} else {
			
			// es war schon mindestens ein LookAhead
			toRun = getNextPlan(lookAheadPlan);
		}

		if (toRun == -1) {
			return false;
		}
		
		if (toRun == runningPlan) {
			return false;
		}
		
		if (Control.debug) {
			Output.println("LookAhead test: " + plans[toRun].getName(), 2);
		}

		if (plans[toRun].isPreRunningable()) {
			if (!aussendienst || Utils.serviceAvalible()) {
				reliableExecution(plans[toRun]);
				lookAheadPlan = toRun;
				return true;
			}
		}

		return false;
	}
	
	public boolean wasLookAhead() {
		return (lookAheadPlan != -1);
	}

	private final int getNextPlan(int i) {
		if (plans.length > i + 1) {
			if (plans[i + 1] == null) {
				return getNextPlan(i + 1);
			}
			return i + 1;
		} else {
			if (infinity)
				return getNextPlan(-1);
			else 
				return -1;
		}
	}

	private final void reliableExecution(PlanObject plan) throws FatalError,
			RestartLater {
		try {
			plan.run();
		} catch (JSONException e) {
			Output.println(plan.getName()
					+ " hat einen unbehandelbaren Fehler erzeugt.", 0);
		}
	}
}
