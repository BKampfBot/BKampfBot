package bkampfbot.utils;

/*
 Copyright (C) 2012  georf@georf.de

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
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;

public class Bank {
	public static boolean putMoney(int money) {

		Utils.getString("city/index");
		Control.quietSleep(500);

		Utils.getString("sparkasse/banker");
		Control.quietSleep(500);

		String einzahlen = Utils.getString("sparkasse/einzahlen");
		Control.quietSleep(500);

		if (money == 0) {

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("max", "1"));
			nvps.add(new BasicNameValuePair("x", "44"));
			nvps.add(new BasicNameValuePair("y", "24"));
			Utils.getString("sparkasse/einzahlen", nvps);

			return true;

		} else {

			if (einzahlen.indexOf(" D-Mark haben um etwas einzuzahlen.") != -1) {
				Output
						.printTabLn("Nicht genug D-Mark verhanden.",
								Output.DEBUG);
				return false;
			}

			int index = einzahlen.indexOf("Du besitzt ");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return false;
			}
			einzahlen = einzahlen.substring(index);

			index = einzahlen.indexOf(" D-Mark.");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return false;
			}
			int moneyNow = Integer.valueOf(einzahlen.substring(0, index)
					.replaceAll("[^0-9]+", ""));

			index = einzahlen.indexOf("Bis auf ein Haushaltsgeld von ");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return false;
			}
			einzahlen = einzahlen.substring(index);

			index = einzahlen.indexOf(" D-Mark kannst du dein Geld");
			if (index == -1) {
				Output.println("Etwas ging schief.", Output.ERROR);
				return false;
			}
			int moneyMin = Integer.valueOf(einzahlen.substring(0, index)
					.replaceAll("[^0-9]+", ""));

			int moneyMax = moneyNow - moneyMin;

			if (money < moneyMax) {
				moneyMax = money;
			}

			// Post zusammenbauen
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("data[Bank][in]", String
					.valueOf(moneyMax)));
			nvps.add(new BasicNameValuePair("x", "110"));
			nvps.add(new BasicNameValuePair("y", "16"));

			Utils.getString("sparkasse/einzahlen", nvps);

			Output.printTabLn("Bringe " + moneyMax + " D-Mark auf die Bank.",
					Output.DEBUG);
			return true;
		}
	}

	public static boolean getMoney(int money) {
		Output.printTab("Hole "
				+ (money == 0 ? "alles Geld" : money + " D-Mark") + " ab: ",
				Output.DEBUG);

		String moneyPage = Utils.getString("sparkasse/auszahlen");
		Control.quietSleep(500);

		int pos = moneyPage.indexOf("<div align=\"center\">");

		if (pos == -1) {
			Output.println("Fehler", Output.DEBUG);
			return false;
		}

		moneyPage = moneyPage.substring(pos);

		pos = moneyPage.indexOf("</div>");

		if (pos == -1) {
			Output.println("Fehler", Output.DEBUG);
			return false;
		}

		moneyPage = moneyPage.substring(0, pos).replaceAll("[^0-9]", "");

		int maxMoney = Integer.valueOf(moneyPage);

		if (maxMoney != 0 && maxMoney >= money) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("x", "104"));
			nvps.add(new BasicNameValuePair("y", "22"));
			nvps.add(new BasicNameValuePair("data[Bank][out]", String
					.valueOf(money == 0 ? maxMoney : money)));
			Utils.getString("sparkasse/auszahlen", nvps);
			Control.quietSleep(500);

			Utils.getString("sparkasse/banker");
			Output.println("OK", Output.DEBUG);
			return true;
		} else {
			Output.println("Fehler", Output.DEBUG);
			return false;
		}

	}
}
