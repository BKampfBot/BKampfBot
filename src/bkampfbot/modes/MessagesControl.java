package bkampfbot.modes;

import java.util.ArrayList;
import java.util.List;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;

public class MessagesControl {
	public MessagesControl() {
		Output.printTabLn("Interaktiver Modus", Output.INFO);
		showPage(1, false);
	}

	public void showPage(int page, boolean delete) {

		try {

			JSONObject list = Utils.getJSON(
					"pns/getFightsData/" + page)
					.getJSONObject("list");

			while (true) {
				// alle kampfnachrichten

				if (list.getString("allNum").equals("0")) {
					Output.println("Keine Nachrichten gefunden", Output.INFO);

					if (page != 1) {
						Output.println("Gehe zurück zu Seite 1:", Output.INFO);
						showPage(1, false);
					}
					return;
				}

				Output
						.print(list.getString("allNum")
								+ " Nachrichten gesamt\n"
								+ "------- NAME -------|- DATUM  -|-|-|\n",
								Output.INFO);

				JSONArray messages = list.getJSONArray("aMsg");
				for (int i = 0; i < messages.length(); i++) {
					JSONObject current = messages.getJSONObject(i);

					String name = current.getString("snd_name");
					if (name.length() > 20) {
						name = name.substring(0, 20);
					} else {
						while (name.length() != 20) {
							name += " ";
						}
					}

					Output.println(name
							+ "|"
							+ current.getString("date")
							+ "|"
							+ ((current.getString("read").equals("no")) ? "O"
									: " ")
							+ "|"
							+ ((current.getString("win").equals("1")) ? "L"
									: "W"), Output.INFO);

				}

				Output.println(
						"1 = Löschen | 2 = Nächste | A = Alle löschen | q = Beenden",
						Output.INFO);
				
				while (true) {
					String input = ""; 
					if (!delete)
						input = Utils.getLine();
					
					
					if (input.equals("1") || delete) {
						// alle löschen

						String ids = "";
						for (int i = 0; i < messages.length(); i++) {
							JSONObject current = messages.getJSONObject(i);
							ids += "," + current.getString("id");
						}
						ids = ids.substring(1);

						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						nvps.add(new BasicNameValuePair("json",
								"{\"redirect\":\"/pns/getFightsData/" + page
										+ "\",\"id\":[" + ids + "]}"));
						try {
							Control.sleep(10);
							
							list = Utils.getJSON(
									"pns/delete",
									nvps).getJSONObject("list");
						} catch (JSONException en) {
							list = Utils
									.getJSON("pns/getFightsData/"
											+ page).getJSONObject("list");
						}
						break;
					}

					if (input.equals("2")) {
						// nächste Seite
						showPage(page + 1, false);
						return;
					}
					
					if (input.equals("A")) {
						showPage(1, true);
					}

					if (input.equals("q")) {
						// Beenden
						return;
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
