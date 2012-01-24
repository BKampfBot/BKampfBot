package bkampfbot.modes;

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

import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.state.Config;

public class TestProxy {

	private final String testUrl = "http://www.georf.de/others/ip/";

	public TestProxy() {
		Config.setHost("");
		Output.println("Deine IP: " + Utils.getString(testUrl), Output.INFO);
	}

}
