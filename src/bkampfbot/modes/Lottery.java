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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import json.JSONObject;
import json.JSONTokener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanObject;

public final class Lottery extends PlanObject{
	public static int[] numbers = new int[5];
	public static boolean random = true;

	public Lottery() {
		run();
	}
	
	public Lottery( JSONObject setup) {
		setName("Lotto");
	}
	
	public void run() {
		Output.printClockLn("Lotterie", 1);

		if (random) {
			Random generator = new Random();

			int n[] = { -1, -1, -1, -1, -1 };

			for (int i = 0; i < 5; i++) {
				int now = generator.nextInt(49);
				while (n[0] == now || n[1] == now || n[2] == now || n[3] == now
						|| n[4] == now) {
					now = generator.nextInt(49);
				}
				Lottery.numbers[i] = now + 1;

				n[i] = now;
			}
		} else {
			String out = "";
			for (int n : numbers) {
				out += n + " ";
			}

			Output.printTabLn("Benutze: " + out, Output.INFO);
		}

		Arrays.sort(Lottery.numbers);

		Utils.visit("lotto/history");

		try {
			String s = Utils.getString("lotto/schein");

			int start = s.indexOf("/img/flash/ankreuzen.swf");
			if (start == -1)
				throw new NotPossible();
			start -= 80;

			s = s.substring(start);

			int lineFront = s.indexOf('{');
			int lineEnd = s.indexOf('}', lineFront + 1);
			s = s.substring(lineFront, lineEnd + 1);
			// Output.noteln(s);
			/*
			 * { quelle: "/img/flash/ankreuzen.swf", minpoints: "300",
			 * currentpoints: "136", jackpot: "132908", numbers: "", ticketid:
			 * "0", STAGE_WIDTH: "708", STAGE_HEIGHT: "569" }
			 */
			JSONObject lottery = new JSONObject(new JSONTokener(s));

			int minPoints = Integer.parseInt(lottery.getString("minpoints"));
			int currentPoints = Integer.parseInt(lottery
					.getString("currentpoints"));

			if (minPoints > currentPoints) {
				throw new NotPossible("Nicht genügend Punkte");
			}

			if (!lottery.getString("numbers").equals("")) {
				throw new NotPossible("Du hast schon Nummern angegeben.");
			}

			// Send numbers
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			String numbers = "";
			for (int i = 0; i < 5; i++) {
				numbers += Lottery.numbers[i];
				if (i != 4)
					numbers += ",";
			}

			nvps.add(new BasicNameValuePair("numbers", numbers));

			// send post
			Utils.getString("lotto/schein", nvps);

			Output.printTabLn("Nummern angekreuzt: " + numbers, 1);

		} catch (Exception e) {
			Output.printTabLn(e.getMessage(), 1);
		}
	}

	private final class NotPossible extends Exception {
		public NotPossible(String string) {
			super(string);
		}

		public NotPossible() {
			super();
		}

		private static final long serialVersionUID = 201102060111L;

	}
}
