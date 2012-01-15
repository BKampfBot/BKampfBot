package bkampfbot.bundesklatsche;

import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class BahnhofField extends Field {

	public BahnhofField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	@Override
	public boolean action() {
		Output.printClockLn("Bahnhof-Feld", Output.INFO);
		return true;
	}

}
