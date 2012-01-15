package bkampfbot.bundesklatsche;

import bkampfbot.Control;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class BahnhofField extends Field {

	public BahnhofField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() {
		Output.printClockLn("Bahnhof-Feld", Output.INFO);
		Control.sleep(10);
		return true;
	}

}
