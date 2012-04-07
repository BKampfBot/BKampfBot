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

import java.util.Random;
import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanObject;

import json.JSONObject;

public final class Gluecksrad extends PlanObject {

	public Gluecksrad() {
		run();
	}
	
	public Gluecksrad(JSONObject setup) {
		setName("Gluecksrad");
	}
		
	public void run() {
		try {
			Output.printClockLn("Glücksrad", 1);

			String s = Utils.getString("guild_challenge/index");
			int i = s.indexOf("var flashvars = {");
			i = s.indexOf("var flashvars = {", i + 1);
			i = s.indexOf("var flashvars = {", i + 1);
			s = s.substring(s.indexOf("guild_id:", i + 1));
			s = s.substring(0, s.indexOf('\n'));
			final String guild_id = s.replaceAll("[^0-9]", "");

			JSONObject result = Utils.getJSON("guild_challenge/getData/"
					+ guild_id);

			if (result.getInt("gluecksrad") == 1) {
				JSONObject win = Utils.getJSON("guild_challenge/gluecksrad/"
						+ (new Random().nextInt(1) == 1 ? 60 : -120));

				if (win.getInt("win") == 1) {
					Output.printTabLn("Glücksrad: gewonnen", 1);
				} else {
					Output.printTabLn("Glücksrad: verloren", 1);
				}
			} else {
				Output.printTabLn("Glücksrad: schon erledigt", 1);
			}
		} catch (Exception e) {
			Output.error(e);
		}
	}
}
