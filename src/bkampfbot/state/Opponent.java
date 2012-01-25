package bkampfbot.state;

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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Utils;
import bkampfbot.exceptions.NotFound;

public class Opponent {
	private String name;
	private String attack;

	private int fights = 0;
	private boolean canFight = true;
	private boolean selected = false;
	private GregorianCalendar date;

	private Opponent() {
	}

	public Opponent(String name, String attack) {
		this.name = name;
		this.attack = attack;
	}

	public final static Opponent getById(String id) throws NotFound {
		Opponent o = new Opponent();

		String profilePage = Utils.getString("characters/profile/" + id);

		int pos = profilePage.indexOf("href=\"/fights/start/");
		if (pos == -1) {
			throw new NotFound();
		}

		String page = profilePage.substring(pos);
		page = page.substring(0, page.indexOf("<img"));

		Pattern p = Pattern.compile("/fights/start/([0-9]+)\"",
				Pattern.MULTILINE);
		Matcher m = p.matcher(page);

		if (!m.find()) {
			throw new NotFound();
		}

		o.setAttack("/fights/start/" + m.group(1));

		pos = profilePage.indexOf("<center><b>");
		if (pos == -1) {
			throw new NotFound();
		}

		page = profilePage.substring(pos);
		page = page.substring(0, page.indexOf("</b></center>")).trim();

		o.setName(page);

		return o;
	}

	public final static Opponent getByName(String name) throws NotFound {
		Opponent o = new Opponent();
		o.setName(name);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("playerName", name));
		try {
			JSONObject ob = Utils.getJSON("fights/searchCharacterJson/1", nvps);

			JSONArray arr = ob.getJSONArray("list");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject player = arr.getJSONObject(i);
				if (player.getString("name").equalsIgnoreCase(name)) {
					o.setAttack(player.getString("attack"));
				}
			}

		} catch (JSONException e) {
		}

		throw new NotFound();
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getAttack() {
		return attack;
	}

	public final void setAttack(String attack) {
		this.attack = attack;
	}

	public final int getFights() {
		return fights;
	}

	public final void addFight() {
		fights++;
	}

	public final boolean canFight() {
		return canFight;
	}

	public final void setDone() {
		canFight = false;
	}

	public final void setNew() {
		canFight = true;
		fights = 0;
		newDate();
	}

	public final boolean equals(Opponent opponent) {
		return (opponent.getAttack().equals(getAttack()));
	}

	public final void select() {
		selected = true;
	}

	public final void unselect() {
		selected = false;
	}

	public final boolean selected() {
		return selected;
	}

	private final void newDate() {
		date = new GregorianCalendar();
		date.setTime(Config.getDate());
	}

	public final void checkNewDay() {
		GregorianCalendar today = new GregorianCalendar();
		today.setTime(Config.getDate());
		today.set(GregorianCalendar.HOUR, 0);
		today.set(GregorianCalendar.MINUTE, 0);

		if (date.before(today)) {
			date = new GregorianCalendar();

			// Set all counters to zero
			setNew();
		}
	}
}
