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

import org.apache.http.NameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.exceptions.FatalError;
import bkampfbot.output.Output;
import bkampfbot.state.User;

public class Skill {
	private final String category;

	private Skill(String category) {
		this.category = category;
	}

	public static Skill get(String category) {
		return new Skill(category);
	}

	public int buy(int count, boolean bank) throws FatalError {
		int bought = 0;

		int thisCount = 0;

		do {
			String character = Control.current.getCharacter();
			character = character.substring(character
					.indexOf("<div class=\"attibutes\">"));

			String link = "";
			String id = "";
			if (category.equals("Mukkies")) {
				link = "strength";
				id = "statStr";
			} else if (category.equals("Schleuderkraft")) {
				link = "intelligence";
				id = "statInt";
			} else if (category.equals("Fitness")) {
				link = "endurance";
				id = "statSta";
			} else if (category.equals("Wahrnehmung")) {
				link = "skill";
				id = "statSkill";
			} else if (category.equals("Glueck")) {
				link = "cognition";
				id = "statCog";
			}

			if (link.equals("")) {
				Output.printTabLn("Es ging was mit der Config schief.",
						Output.INFO);
				return 0;
			}

			link = "characters/charLeft/" + link;
			id = " id=\"" + id + "\"";

			int searchIndex = 0;

			// search for id
			searchIndex = character.indexOf(id);
			if (searchIndex < 0)
				return 0;
			String thisSkill = character.substring(searchIndex);

			searchIndex = thisSkill.indexOf("Kosten Steigerung: <span");
			if (searchIndex < 0)
				return 0;
			String increaseCost = thisSkill.substring(searchIndex);

			searchIndex = increaseCost.indexOf(">");
			if (searchIndex < 0)
				return 0;
			increaseCost = increaseCost.substring(searchIndex);

			searchIndex = increaseCost.indexOf("</span>");
			if (searchIndex < 0)
				return 0;
			increaseCost = increaseCost.substring(0, searchIndex).replaceAll(
					"[^0-9]", "");

			Output.printTabLn("Du hast " + User.getGold() + " D-Mark und "
					+ category + " kostet " + Integer.valueOf(increaseCost)
					+ " D-Mark", Output.DEBUG);

			int needMoney = Integer.valueOf(increaseCost) - User.getGold();
			if (needMoney > 0) {

				if (bank) {

					if (!Bank.getMoney(needMoney)) {
						return 0;
					}

				} else {
					return 0;
				}
			}

			Utils.getString(link, new ArrayList<NameValuePair>());
			Output.printTabLn("Kaufe " + category + " für "
					+ Integer.valueOf(increaseCost) + " D-Mark", Output.INFO);

			User.setGold(User.getGold() - Integer.valueOf(increaseCost));

			bought++;

			thisCount++;
		} while (count < 0 || thisCount < count);

		return bought;
	}
}
