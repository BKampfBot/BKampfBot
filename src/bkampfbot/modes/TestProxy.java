package bkampfbot.modes;

import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.state.Config;

public class TestProxy {

	private final String testUrl = "http://www.georf.de/others/ip/";
	
	public TestProxy () {
		Config.setHost("");
		Output.println("Deine IP: " + Utils.getString(testUrl), Output.INFO);
	}

}
