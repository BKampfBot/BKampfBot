package bkampfbot.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.exception.FatalError;
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

			if (User.getGold() < Integer.valueOf(increaseCost)) {
				return 0;
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
