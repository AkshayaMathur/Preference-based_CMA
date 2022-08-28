package com.CMA;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author akshayamathur
 * 
 *         Contains all the Preference Model
 *
 */
public class PreferenceModels {

	/**
	 * 
	 * Numeric Preference
	 * 
	 * @param priorityMap Preference map
	 * @param cutOff      threshold value
	 * @return
	 */
	public static List<Miner> numericalPreference(Map<String, Integer> priorityMap, int cutOff) {
		String finalQuery = "";
		boolean containsPrev = false;

		if (priorityMap.containsKey(Constants.STAKE)) {
			finalQuery += "(stake*" + Integer.toString(priorityMap.get(Constants.STAKE)) + ")";
			containsPrev = true;
		}
		if (priorityMap.containsKey(Constants.PROCESSINGPOWER)) {
			finalQuery += (containsPrev ? " + " : "") + "(processPower*"
					+ Integer.toString(priorityMap.get(Constants.PROCESSINGPOWER)) + ")";
			containsPrev = true;
		}
		if (priorityMap.containsKey(Constants.COST)) {
			finalQuery += (containsPrev ? " + " : "") + "(cost*" + Integer.toString(priorityMap.get(Constants.COST))
					+ ")";
			containsPrev = true;
		}
		if (priorityMap.containsKey(Constants.DISKSPACE)) {
			finalQuery += (containsPrev ? " + " : "") + "(diskSpace*"
					+ Integer.toString(priorityMap.get(Constants.DISKSPACE)) + ")";
		}
		try {
			return Network.getMinerBasedOnScore(finalQuery, cutOff);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * Priority Preference
	 * 
	 * @param preference List of preferred parameter
	 * @param cutOff     threshold value
	 * @return
	 */
	public static List<Miner> prioritizedPreference(List<String> preference, int cutOff) {
		try {
			String finalQuery = "";

			for (int i = 0; i < preference.size(); i++) {
				String string = preference.get(i);

				if (string.equals(Constants.STAKE)) {
					finalQuery += "stake DESC";
				} else if (string.equals(Constants.PROCESSINGPOWER)) {
					finalQuery += "processPower DESC";
				} else if (string.equals(Constants.COST)) {
					finalQuery += "cost DESC";

				} else if (string.equals(Constants.DISKSPACE)) {
					finalQuery += "diskSpace DESC";
				}

				if (i < preference.size() - 1) {
					finalQuery += ", ";
				} else {
					finalQuery += " LIMIT " + Integer.toString(cutOff);
				}
			}

			return Network.getMinersBasedOnPriority(finalQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * Balanced Preference
	 * 
	 * @param preference List of preferred parameter
	 * @param cutOff     threshold value
	 * @return
	 */
	public static List<Miner> balancedPreference(List<String> preference, int cutOff) {
		String finalQuery = "";
		boolean containsPrev = false;

		if (preference.contains(Constants.STAKE)) {
			finalQuery += "stake";
			containsPrev = true;
		}
		if (preference.contains(Constants.PROCESSINGPOWER)) {
			finalQuery += (containsPrev ? " + " : "") + "processPower";
			containsPrev = true;
		}
		if (preference.contains(Constants.COST)) {
			finalQuery += (containsPrev ? " + " : "") + "cost";
			containsPrev = true;
		}
		if (preference.contains(Constants.DISKSPACE)) {
			finalQuery += (containsPrev ? " + " : "") + "diskSpace";
		}
		try {
			return Network.getMinerBasedOnScore(finalQuery, cutOff);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * Randomly generate numeric preference
	 * 
	 * @return Map of preferred parameter
	 */
	public static Map<String, Integer> generateNumericPreference() {

		int numOfPreferences = generateRandomNum(1, 3);

		Map<String, Integer> priority = new HashMap<String, Integer>();

		for (int i = 0; i < numOfPreferences; i++) {
			int value = generateRandomNum(1, 4);

			priority.put(getPreferenceName(value), generateRandomNum(0, 100));
		}

		return priority;
	}

	/**
	 * 
	 * Randomly generate Priority & Balanced preference
	 * 
	 * @return List of preferred parameter
	 */
	public static List<String> generatePriorityAndBalancePreference() {
		List<String> pref = new ArrayList<>();

		int numOfPreferences = generateRandomNum(1, 3);
		for (int i = 0; i < numOfPreferences; i++) {
			int value = generateRandomNum(1, 4);

			pref.add(getPreferenceName(value));
		}
		return pref;

	}

	/**
	 * 
	 * Generate Random number
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @return random number
	 */
	public static int generateRandomNum(int min, int max) {
		int range = max - min + 1;
		return (int) (Math.random() * range) + min;
	}

	/**
	 * 
	 * Get name of preferred parameter name
	 * 
	 * @param num
	 * @return get the parameter name
	 */
	public static String getPreferenceName(int num) {
		if (num == 1) {
			return Constants.PROCESSINGPOWER;
		} else if (num == 2) {
			return Constants.STAKE;
		} else if (num == 3) {
			return Constants.COST;
		} else {
			return Constants.DISKSPACE;
		}
	}

}
