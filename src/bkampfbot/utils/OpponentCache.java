package bkampfbot.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import bkampfbot.state.Config;
import bkampfbot.state.Opponent;

public class OpponentCache {

	private ArrayList<Opponent> highMoney;
	private Opponent[] lowMoney;
	private int lowMoneyPointer = 0;
	private Opponent[] bad;
	private int badPointer = 0;

	private Calendar countDate;

	private static HashMap<String, OpponentCache> cache = new HashMap<String, OpponentCache>();

	private OpponentCache() {
		highMoney = new ArrayList<Opponent>();
		lowMoney = new Opponent[100];
		bad = new Opponent[100];

		countDate = new GregorianCalendar();
		countDate.setTime(Config.getDate());
	}

	public static OpponentCache getInstance(String scope) {
		if (cache.containsKey(scope)) {
			return cache.get(scope);
		}

		OpponentCache instance = new OpponentCache();
		cache.put(scope, instance);
		return instance;
	}

	public static void reset() {
		cache = new HashMap<String, OpponentCache>();
	}

	public boolean removeHighMoney(Opponent opp) {
		return highMoney.remove(opp);
	}

	/**
	 * Add an opponent to the "bad opponent" list
	 */
	public final void addBad(Opponent opp) {
		bad[badPointer] = opp;
		badPointer++;

		if (badPointer == bad.length) {
			badPointer = 0;
		}
	}

	/**
	 * Add an opponent to the "high money" list
	 */
	public final void addHighMoney(Opponent opponent) {

		// is already inside?
		for (Opponent a : highMoney) {
			if (a.equals(opponent)) {
				return;
			}
		}

		highMoney.add(opponent);
	}

	/**
	 * Add an opponent to the "low money" list
	 */
	public final void addLowMoney(Opponent opponent) {

		lowMoney[lowMoneyPointer] = opponent;
		lowMoneyPointer++;

		if (lowMoneyPointer == lowMoney.length) {
			lowMoneyPointer = 0;
		}
	}

	/**
	 * find a opponent from the "high money" list, which can fight
	 * 
	 * @return null if nobody found or the opponent
	 */
	public final Opponent findHighMoney() {
		checkDateCounter();

		for (Opponent opp : highMoney) {
			if (opp.canFight()) {
				return opp;
			}
		}

		return null;
	}

	/**
	 * Reset the counter if started a new day
	 */
	private final void checkDateCounter() {
		Calendar today = new GregorianCalendar();
		today.setTime(Config.getDate());
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);

		if (countDate.before(today)) {
			countDate = new GregorianCalendar();

			// Set all counters to zero
			for (Opponent opp : highMoney) {
				opp.setNew();
			}
		}
	}

	/**
	 * Returns true if the opponent is on the "low money" list or if the
	 * opponent is on the "max fight" list.
	 * 
	 * @return true if on the list
	 */
	public final boolean isBadOpponent(Opponent opponent) {

		for (int i = 0; i < lowMoney.length; i++) {
			if (lowMoneyPointer == i && lowMoney[i] == null) {
				break;
			}
			if (lowMoney[i].equals(opponent)) {
				return true;
			}
		}

		for (Opponent o : bad) {
			if (o != null && o.equals(opponent)) {
				return true;
			}
		}

		return false;
	}
}
