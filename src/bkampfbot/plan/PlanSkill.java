package bkampfbot.plan;

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

import json.JSONException;
import json.JSONObject;

import org.apache.http.NameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.exception.FatalError;
import bkampfbot.output.Output;
import bkampfbot.state.User;

/**
 * PlanSkill benötigt folgende Konfiguration: {"Skill":{"Kategorie":"Mukkies",
 * "Anzahl":1}}
 * 
 * @author georf
 * 
 */
public final class PlanSkill extends PlanObject {
	private String category = "";
	private int count = -1;

	public PlanSkill(JSONObject object) throws FatalError {
		this.setName("Skill");

		try {
			JSONObject skill = object.getJSONObject(getName());
			category = skill.getString("Kategorie");

			try {
				count = skill.getInt("Anzahl");
			} catch (JSONException e) {
			}
		} catch (JSONException e) {
			throw new FatalError("Config error: Skill config is bad");
		}

	}

	public final void run() throws FatalError {
		Output.printClockLn("-> Skill (" + category + ")", Output.INFO);
		/*
		 * <span style='color: #000000;font-size:11px;'>Kosten Steigerung:
		 * <span>95 </span>
		 * 
		 * Mukkies id="statStr" /characters/charLeft/strength
		 * 
		 * Schleuderkraft id="statInt" /characters/charLeft/intelligence
		 * 
		 * Fitness id="statSta" /characters/charLeft/endurance
		 * 
		 * Wahrnehmung id="statSkill" /characters/charLeft/skill
		 * 
		 * Glück id="statCog" /characters/charLeft/cognition
		 * 
		 * 
		 * </div> <script type="text/javascript"> var vereinbonus = 0; var
		 * baseStr = 26; var baseInt = 28; var baseSkill = 133; var baseSta =
		 * 247; var baseCog = 436; var baseArmor = 0; var charLevel = 50;
		 * </script>
		 */

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
				return;
			}

			link = "characters/charLeft/" + link;
			id = " id=\"" + id + "\"";

			int searchIndex = 0;

			// search for id
			searchIndex = character.indexOf(id);
			if (searchIndex < 0)
				return;
			String thisSkill = character.substring(searchIndex);

			searchIndex = thisSkill.indexOf("Kosten Steigerung: <span");
			if (searchIndex < 0)
				return;
			String increaseCost = thisSkill.substring(searchIndex);

			searchIndex = increaseCost.indexOf(">");
			if (searchIndex < 0)
				return;
			increaseCost = increaseCost.substring(searchIndex);

			searchIndex = increaseCost.indexOf("</span>");
			if (searchIndex < 0)
				return;
			increaseCost = increaseCost.substring(0, searchIndex).replaceAll(
					"[^0-9]", "");

			Output.printTabLn("Du hast " + User.getGold() + " D-Mark und " + category+" kostet "
					+ Integer.valueOf(increaseCost)+ " D-Mark", Output.DEBUG);

			if (User.getGold() < Integer.valueOf(increaseCost)) {
				return;
			}

			Utils.getString(link, new ArrayList<NameValuePair>());
			Output.printTabLn("Kaufe " + category + " für "
					+ Integer.valueOf(increaseCost) + " D-Mark", Output.INFO);

			User.setGold(User.getGold() - Integer.valueOf(increaseCost));

			thisCount++;
		} while (count < 0 || thisCount < count);
	}

	@Override
	public boolean isPreRunningable() {
		return true;
	}
}
