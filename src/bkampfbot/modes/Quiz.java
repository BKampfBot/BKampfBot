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
import java.util.List;
import java.util.Random;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;

public final class Quiz {

	public static int wrongAnswerCount = 0;

	public Quiz() {
		run();
	}
	
	public Quiz(int wrongAnswerCount) {
		Quiz.wrongAnswerCount = wrongAnswerCount;
	}
	
	/**
	 * Gibt Anzahl der richtigen Antworten zurück.
	 * @return
	 */
	public int run() {
		int count = 0;
		
		if (wrongAnswerCount == 0) {
			Output.printClockLn("Tagesquiz", 1);
		} else {
			Output.printClockLn("Tagesquiz mit " + wrongAnswerCount
					+ " falschen Antworten", 1);
		}

		Utils.visit("quiz/");

		String secret = "097ab47f37712cc85170b78700daedea";

		try {
			JSONObject result = Utils.getJSON("quiz/GetQuestions/gold/"
					+ secret);
			// JSONObject result = new JSONObject(new
			// JSONTokener("{\"seconds\":120,\"list\":{\"18719189\":{\"question\":\"Wie hei\u00dft die Wissenschaft von den fossilen Pflanzen?\",\"answered\":0,\"choices\":{\"2\":\"Pal\u00e4obotanik\",\"4\":\"Arch\u00e4obotanik\",\"3\":\"Antikobotanik\",\"1\":\"Historobotanik\"},\"correctanswer\":\"8406724b4f7eb2dda648804a16957103\"},\"18719186\":{\"question\":\"Welche Prominente war in den so genannten Nipplegate-Skandal verwickelt?\",\"answered\":0,\"choices\":{\"2\":\"Janet Jackson\",\"3\":\"Paris Hilton\",\"4\":\"Lindsay Lohan\",\"1\":\"Halle Berry\"},\"correctanswer\":\"e1c55d56496ea301db6e94489a5dae54\"},\"18719184\":{\"question\":\"In welcher dieser St\u00e4dte findet man kein Schloss?\",\"answered\":0,\"choices\":{\"2\":\"Wilhelmshaven\",\"3\":\"Weimar\",\"4\":\"Dresden\",\"1\":\"Berlin\"},\"correctanswer\":\"a1776b4e50e4594533bc3d5ef88961d1\"},\"18719183\":{\"question\":\"Welcher dieser TV-Sender ist im Besitz von Silvio Berlusconi?\",\"answered\":0,\"choices\":{\"1\":\"Tele 5\",\"2\":\"Neun Live\",\"3\":\"Viva\",\"4\":\"RTL 2\"},\"correctanswer\":\"94383bf8e653a8a75ac380cd355fa776\"},\"18719181\":{\"question\":\"Wobei handelt es sich um eine Film-Trilogie?\",\"answered\":0,\"choices\":{\"4\":\"Zur\u00fcck in die Zukunft\",\"2\":\"Titanic\",\"1\":\"A beautiful Mind\",\"3\":\"Contact\"},\"correctanswer\":\"e796523f8ef279759d33ad5e5ed58e69\"},\"18719188\":{\"question\":\"Wer spielt die Hauptrolle in Die Legende des Zorro?\",\"answered\":0,\"choices\":{\"4\":\"Antonio Banderas\",\"3\":\"Sean Connery\",\"2\":\"Alain Delon\",\"1\":\"Tom Hanks\"},\"correctanswer\":\"0a73d315eaab9e15f448c87009459804\"},\"18719190\":{\"question\":\"Welches Ma\u00df gab ein Bauer fr\u00fcher f\u00fcr eine Ackerfl\u00e4che an?\",\"answered\":0,\"choices\":{\"1\":\"Morgen\",\"4\":\"Gestern\",\"3\":\"Heute\",\"2\":\"Heurig\"},\"correctanswer\":\"3fcefa841597b9ee846d71612fab785b\"},\"18719185\":{\"question\":\"Wie hei\u00dft der Baum, der traditionell in Bayern Paaren in den Garten gepflanzt wird, die nach siebenj\u00e4hrigem Zusammenleben noch immer nicht geheiratet haben?\",\"answered\":0,\"choices\":{\"3\":\"Hungerbaum\",\"4\":\"Durstbaum\",\"2\":\"Tr\u00f6delbaum\",\"1\":\"Ewigkeitsbaum\"},\"correctanswer\":\"ea0e664495ffe6d0c219e40e736a1c92\"},\"18719187\":{\"question\":\"Wie ruft man in Berlin zu Karneval?\",\"answered\":0,\"choices\":{\"4\":\"Berlin Hajo!\",\"1\":\"Berlin Alaaf!\",\"3\":\"Berlin Helau!\",\"2\":\"Berlin Aja!\"},\"correctanswer\":\"831971d1c96e254a791a038e728b7272\"},\"18719182\":{\"question\":\"Welches Tier gilt als geheimes Wappentier Islands?\",\"answered\":0,\"choices\":{\"3\":\"Papageientaucher\",\"4\":\"Islandpferd\",\"2\":\"Polarfuchs\",\"1\":\"Eissturmvogel\"},\"correctanswer\":\"8130e01dac15f32b09bd4b7f5b6c9293\"}}}"));

			JSONObject list;
			String questions[];
			try {
				list = result.getJSONObject("list");
				questions = JSONObject.getNames(list);
			} catch (JSONException e) {
				Output.printTabLn("Quiz wurde heute schon erledigt.", 1);
				return 0;
			}

			Output.printTabLn("Hole Fragen:\n", 2);

			JSONArray answers = new JSONArray();
			JSONObject answer;

			for (String quest : questions) {
				JSONObject question = list.getJSONObject(quest);
				JSONObject choices = question.getJSONObject("choices");

				answer = new JSONObject();
				answer.put("id", Integer.valueOf(quest));

				int selected;

				if (wrongAnswerCount != 0) {
					selected = new Random().nextInt(4) + 1;
					if (selected != Integer
							.valueOf(choices.getInsertOrder()[0])) {
						wrongAnswerCount--;
					}
				} else {
					selected = Integer.valueOf(choices.getInsertOrder()[0]);
					count++;
				}

				answer.put("number", selected);
				Output.printTabLn(question.getString("question"), 2);
				Output.printTabLn(choices.getString(String.valueOf(selected))
						+ ((selected != Integer.valueOf(choices
								.getInsertOrder()[0])) ? " (falsch)" : "")
						+ "\n", 2);

				answer.put("answer", ((selected != Integer.valueOf(choices
						.getInsertOrder()[0])) ? 0 : 1));
				answers.put(answer);
			}

			int time = new Random().nextInt(10) + 30;
			Control.sleep(time * 10, 2);

			List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
			nvps2.add(new BasicNameValuePair("result", answers.toString()));
			nvps2.add(new BasicNameValuePair("secret", secret));
			nvps2.add(new BasicNameValuePair("time", String.valueOf(time)));

			// errorStatus=ok&reward=108&rewardtype=gold
			String result2 = Utils.getString("quiz/GetReward", nvps2);

			if (!result2.matches(".*errorStatus=ok.*")) {
				Output.printTabLn("Es ging was schief.", 1);
			} else {
				Output.printTabLn("Bekomme " + result2.replaceAll("[^0-9]", "")
						+ " D-Mark", 1);
			}
		} catch (JSONException e) {
			Output.error(e);
		}
		
		return count;
	}
}