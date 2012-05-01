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
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import bkampfbot.Utils;
import bkampfbot.output.Output;

public class Strategie {
	public static final String[] parts = {
		"armleft",
		"armright",
		"body",
		"head",
		"legleft",
		"legright"
	};
	
	private String[] attack = new String[3];
	private String[] defens = new String[3];
	
	private Strategie() {
	}
	
	public Strategie(String a1, String a2, String a3, String d1, String d2, String d3) {
		attack[0] = a1;
		attack[1] = a2;
		attack[3] = a3;
		defens[0] = d1;
		defens[1] = d2;
		defens[2] = d3;
	}
	
	
	/*
	 * GET /fights/tacticData HTTP/1.1\r\n
	 * {"taktik":{"defens":["head","legright","armleft"],"attack":["head","legright","armleft"]},"gender":"m"}
	 * 
	 * POST /fights/tacticSave HTTP/1.1\r\n
	 * json={"defens":["head","legright","armright"],"attack":["head","legright","armleft"]}
	 */
	
	public static Strategie getRandom() {
		Strategie s = new Strategie();
		
		String a1 = parts[(new Random()).nextInt(parts.length)];
		String a2 = null;
		do {
			a2 = parts[(new Random()).nextInt(parts.length)];
		} while(a2.equals(a1));
		String a3 = null;
		do {
			a3 = parts[(new Random()).nextInt(parts.length)];
		} while(a3.equals(a1) || a3.equals(a2));
		
		String d1 = parts[(new Random()).nextInt(parts.length)];
		String d2 = null;
		do {
			d2 = parts[(new Random()).nextInt(parts.length)];
		} while(d2.equals(d1));
		String d3 = null;
		do {
			d3 = parts[(new Random()).nextInt(parts.length)];
		} while(d3.equals(d1) || d3.equals(d2));
		
		s.setAttack(a1, a2, a3);
		s.setDefens(d1, d2, d3);
		
		return s;
	}
	
	public Strategie setDefens(String[] tactics) {
		
		if (tactics.length > 0) {
			defens[0] = tactics[0];
		}
		
		if (tactics.length > 1) {
			defens[1] = tactics[1];
		}
		
		if (tactics.length > 2) {
			defens[2] = tactics[2];
		}
		
		while(defens[0].equals(defens[1])) {
			defens[1] = parts[(new Random()).nextInt(parts.length)];
		} 
		while(defens[0].equals(defens[2]) || defens[1].equals(defens[2])) {
			defens[2] = parts[(new Random()).nextInt(parts.length)];
		} 
		
		return this;
	}
	
	public Strategie setDefens(String first, String second, String third) {
		defens[0] = first;
		defens[1] = second;
		defens[2] = third;
		
		return this;
	}
	
	public Strategie setAttack(String first, String second, String third) {
		attack[0] = first;
		attack[1] = second;
		attack[2] = third;
		
		return this;
	}
	
	public Strategie save() throws JSONException {
		return save(false);
	}
	
	public Strategie save(boolean output) throws JSONException {

		JSONArray a = new JSONArray(attack);
		JSONArray d = new JSONArray(defens);
		JSONObject j = new JSONObject();
		j.put("attack", a);
		j.put("defens", d);
		
		if (output) {
			Output.printTabLn("Speichere neue Strategie", Output.DEBUG);
			Output.printTabLn("    Abwehr:  "+d, Output.DEBUG);
			Output.printTabLn("    Angriff: "+a, Output.DEBUG);
		}
		
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("json", j.toString()));
		Utils.getString("fights/tacticSave", nvps);
		
		return this;
	}
}
