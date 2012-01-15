package bkampfbot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import bkampfbot.Utils;

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
	
	public void setDefens(String first, String second, String third) {
		defens[0] = first;
		defens[1] = second;
		defens[2] = third;
	}
	
	public void setAttack(String first, String second, String third) {
		attack[0] = first;
		attack[1] = second;
		attack[2] = third;
	}
	
	public void save() throws JSONException {
		JSONArray a = new JSONArray(attack);
		JSONArray d = new JSONArray(defens);
		JSONObject j = new JSONObject();
		j.put("attack", a);
		j.put("defens", d);
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("json", j.toString()));
		Utils.getString("fights/tacticSave", nvps);
	}
}
