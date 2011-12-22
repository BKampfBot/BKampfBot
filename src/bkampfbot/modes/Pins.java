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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;


import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import json.JSONTokener;

public final class Pins {

	private ArrayList<Integer> toBuy;

	public static String in;

	public Pins() {
		Output.printClockLn("Pins", 1);

		String input = in.replaceAll("[^0-9,]", "");
		input = "{\"buy\":[" + input + "]}";
		try {
			JSONObject buy = new JSONObject(new JSONTokener(input));
			JSONArray arr = buy.getJSONArray("buy");

			this.toBuy = new ArrayList<Integer>(arr.length());

			for (int i = 0; i < arr.length(); i++) {
				this.toBuy.add(i, arr.getInt(i));
			}

			arr = null;

			while (toBuy.size() > 0) {
				if (this.buy(toBuy.get(0))) {
					this.toBuy.remove(0);
				}
				Control.sleep(10);
			}
		} catch (Exception e) {
			Output.error(e);
		}
	}

	private void addToList(int pId, int count) {
		while (count < 0) {
			this.addToList(pId);
			count++;
		}
	}

	private void addToList(int pId) {
		this.toBuy.add(0, pId);
	}

	private ArrayList<ArrayList<Integer>> getPins() throws JSONException {
		JSONObject result = Utils
				.getJSON("items/getPinData/");
		/*
		 * {"items":[...], "pins":[
		 * {"id":"11446","character_id":"163988","typ":"1"
		 * ,"stufe":"1","created":""},
		 * {"id":"11452","character_id":"163988","typ"
		 * :"2","stufe":"1","created":""}, ],
		 */

		JSONArray pins = result.getJSONArray("pins");
		ArrayList<ArrayList<Integer>> cache = new ArrayList<ArrayList<Integer>>(
				8);
		for (int i = 0; i < 8; i++) {
			cache.add(i, new ArrayList<Integer>());
		}

		for (int i = 0; i < pins.length(); i++) {
			JSONObject o = pins.getJSONObject(i);
			if (o.getString("stufe").equals("1")) {
				cache.get(Integer.valueOf(o.getString("typ")) - 1).add(
						Integer.valueOf(o.getString("id")));
			}
		}

		return cache;
	}

	private boolean buy(int type) throws IOException, JSONException {
		ArrayList<ArrayList<Integer>> cache = this.getPins();
		Output.printTabLn("Kaufe " + type, 1);

		switch (type) {
		case 11:
		case 21:
		case 31:
		case 41:
			Utils.visit("items/kaufen/"
					+ type + "/0");
			return true;

		case 51:
			if (cache.get(0).size() >= 3 && cache.get(3).size() >= 1) {
				this.buy(cache.get(0).get(0), cache.get(0).get(1), cache.get(0)
						.get(2), cache.get(3).get(0));
				return true;
			} else {
				if (cache.get(0).size() < 3) {
					this.addToList(11, cache.get(0).size() - 3);
				}
				if (cache.get(3).size() < 1) {
					this.addToList(41);
				}
				Output.printTabLn("Nicht genügend Pins", 2);
				return false;
			}

		case 61:
			if (cache.get(0).size() >= 1 && cache.get(1).size() >= 1
					&& cache.get(2).size() >= 1 && cache.get(3).size() >= 1) {
				this.buy(cache.get(0).get(0), cache.get(1).get(0), cache.get(2)
						.get(0), cache.get(3).get(0));
				return true;
			} else {
				if (cache.get(0).size() < 1) {
					this.addToList(11);
				}
				if (cache.get(1).size() < 1) {
					this.addToList(21);
				}
				if (cache.get(2).size() < 1) {
					this.addToList(31);
				}
				if (cache.get(3).size() < 1) {
					this.addToList(41);
				}
				Output.printTabLn("Nicht genügend Pins", 2);
				return false;
			}
		case 71:
			if (cache.get(0).size() >= 1 && cache.get(1).size() >= 3) {
				this.buy(cache.get(0).get(0), cache.get(1).get(0), cache.get(1)
						.get(1), cache.get(1).get(2));
				return true;
			} else {
				if (cache.get(0).size() < 1) {
					this.addToList(11);
				}
				if (cache.get(1).size() < 3) {
					this.addToList(21, cache.get(1).size() - 3);
				}
				Output.printTabLn("Nicht genügend Pins", 2);
				return false;
			}
		case 81:
			if (cache.get(1).size() >= 2 && cache.get(2).size() >= 1
					&& cache.get(3).size() >= 1) {
				this.buy(cache.get(1).get(0), cache.get(1).get(1), cache.get(2)
						.get(0), cache.get(3).get(0));
				return true;
			} else {
				if (cache.get(1).size() < 2) {
					this.addToList(21, cache.get(1).size() - 2);
				}
				if (cache.get(2).size() < 1) {
					this.addToList(31);
				}
				if (cache.get(3).size() < 1) {
					this.addToList(41);
				}

				Output.printTabLn("Nicht genügend Pins", 2);
				return false;
			}

		default:
			if (type > 11 && type < 83 && type % 10 == 2) {

				int type2 = type;
				type2 -= 2;
				type2 /= 10;
				type2 -= 1;

				if (cache.get(type2).size() >= 4) {
					this.buy(cache.get(type2).get(0), cache.get(type2).get(1),
							cache.get(type2).get(2), cache.get(type2).get(3));
					return true;
				} else {
					this.addToList(type - 1, cache.get(type2).size() - 4);
					Output.printTabLn("Nicht genügend Pins", 2);
					return false;
				}
			}
		}
		return false;
	}

	private void buy(int p1, int p2, int p3, int p4) {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("pin4", String.valueOf(p4)));
		nvps.add(new BasicNameValuePair("pin3", String.valueOf(p3)));
		nvps.add(new BasicNameValuePair("pin2", String.valueOf(p2)));
		nvps.add(new BasicNameValuePair("pin1", String.valueOf(p1)));

		Utils.getString(
				"items/check/1/fortunes", nvps);
	}
}
