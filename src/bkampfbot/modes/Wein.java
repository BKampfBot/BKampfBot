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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanObject;


import json.JSONObject;

public final class Wein extends PlanObject {

	public Wein() {
		run();
	}
	
	public Wein(JSONObject setup) {
		setName("Weinkeller");
	}
	
	public void run() {
		Output.printClockLn("Weinfässer", 1);
		
		try {

			String s = Utils
					.getString("guild_challenge/index");
			int i = s.indexOf("var flashvars = {");
			i = s.indexOf("var flashvars = {", i + 1);
			i = s.indexOf("var flashvars = {", i + 1);
			s = s.substring(s.indexOf("guild_id:", i + 1));
			s = s.substring(0, s.indexOf('\n'));
			final String guild_id = s.replaceAll("[^0-9]", "");

			JSONObject result = Utils
					.getJSON("guild_challenge/getData/"
							+ guild_id);

			if (result.getInt("game") == 1) {

				Utils
						.visit("games/kisten/0");

				JSONObject befor = Utils
										.getJSON("games/getGameWarehouse");

				int wait = new Random().nextInt(30) + 30;
				Control.sleep(wait * 10, 2);

				String md5;
				try {
					MessageDigest m = MessageDigest.getInstance("MD5");
					byte[] data = ("U9ZF6FG7WDDSW453WIN" + befor
							.getString("hash")).getBytes();
					m.update(data, 0, data.length);
					BigInteger in = new BigInteger(1, m.digest());
					md5 = String.format("%1$032X", in);
				} catch (NoSuchAlgorithmException e) {
					Output.error(e);
					return;
				}

				md5 = md5.toLowerCase();

				JSONObject win = Utils
										.getJSON("games/punkteWarehouse/"
												+ md5);

				if (win.getInt("result") == 1) {
					Output.printTabLn("Weinkeller gewonnen", 1);
				} else {
					Output.printTabLn("Weinkeller verloren", 1);
				}
			} else {
				Output.printTabLn("Weinkeller heute schon erledigt", 1);
			}
		} catch (Exception e) {
			Output.error(e);
		}
	}
}
