package bkampfbot.bundesklatsche;

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

import json.JSONException;
import bkampfbot.Control;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanBundesklatsche;

public class VierGewinntField extends Field {

	public VierGewinntField(PlanBundesklatsche klatsche) {
		super(klatsche);
	}

	/*
	 * { "round_end": 0, "char": { "klatschen": "13", "rolls_ok": 1, "beute":
	 * "576", "round": 22, "max_rolls": 8, "hof": 10253, "run": true, "var1":
	 * "504 DM sichere Beute", "rolls": "3", "sk8": "0", "last_klatschen": "8",
	 * "backpfeifen": "150", "sknum1": "0", "viergewinnt": "4", "last_payout":
	 * "21", "last_sicher": "0", "cont": "viergewinnt", "character_id":
	 * "615397", "race_id": "2", "rolls_free": 1, "skvar1": "", "last_meister":
	 * "0", "sicher": "0", "meister": "0", "modified": "2012-02-16 22:39:54",
	 * "sk6": "0", "sk7": "0", "last_round": "21", "sk4": "0", "sk5": "0",
	 * "sk2": "4", "sk3": "0", "prize": 0, "sk1": "4", "num2": "0",
	 * "meister_hof": 7903, "num1": "504", "figur_pos": "21", "roll_date":
	 * "2012-02-16" }, "action": { "cont": "" }, "error_txt": "", "payout_arr":
	 * 0, "payout": 0, "card_error": "" }
	 */

	@Override
	public boolean action() throws JSONException {

		if (getResult().getJSONObject("char").getString("viergewinnt").equals(
				"4")) {
			Output.printTabLn("Vier gewinnt steht auf 4: "
					+ getResult().getJSONObject("char").getString("var1"),
					Output.INFO);
		} else {
			Output.printTabLn("Vier gewinnt", Output.INFO);
		}
		Control.sleep(10);
		return true;
	}

}
