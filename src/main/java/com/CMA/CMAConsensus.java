package com.CMA;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author akshayamathur
 *
 */
public class CMAConsensus {

	/**
	 * Execute Preference-based Committee member auction consensus algorithm
	 */
	public static void executeCMAAlgo() {

		List<Long> finalTimes = new ArrayList<Long>();

		Map<String, Integer> selectedComitteeMembers = new HashMap<>();
		Map<String, Integer> selectedMiner = new HashMap<>();

		Map<String, Integer> priority = new HashMap<>();

		// For experiment 1
		priority.put(Constants.PROCESSINGPOWER, 90);
		priority.put(Constants.STAKE, 10);

		List<String> pref = new ArrayList<>();
		pref.add(Constants.PROCESSINGPOWER);
		pref.add(Constants.STAKE);

		for (int i = 0; i < 10; i++) {
			try {
				System.out.println("RUNNING FOR " + i);
				final int cutOffScore = PreferenceModels.generateRandomNum(Constants.MIN_GEN_VAL,
						Constants.MAX_GEN_VAL);
				// For other experiment, randomly generate preferences.
				// Map<String, Integer> priority = PreferenceModels.generateNumericPreference();
				// List<String> pref = PreferenceModels.generatePriorityAndBalancePreference();

				// Generate Transactions
				generateTransactions();

				// Get system time
				long startTime = System.nanoTime();

				// Invoke committee member election
				List<Miner> committeeMembers = commiteeMemberElection(0, priority, cutOffScore);
				// List<Miner> committeeMembers = commiteeMemberElection(1, pref, cutOffScore);

				// Generate Trapdoor key for the Chameleon hash function
				ArrayList<ArrayList<Byte>> trapDoorKeys = CryptoService.generateTrapdoorKeys(committeeMembers.size());

				// Trapdoor key distribution
				distributeKeysToCommitteeMembers(committeeMembers, trapDoorKeys);

				// Start Mining
				startMining(committeeMembers);

				// Get system time
				long endTime = System.nanoTime();

				// Calculate Time
				long totalTime = endTime - startTime;

				System.out.println(totalTime);
				finalTimes.add(totalTime);

				// Calculate Members elected
				for (Miner member : committeeMembers) {
					if (selectedComitteeMembers.containsKey(member.getName())) {
						selectedComitteeMembers.put(member.getName(),
								selectedComitteeMembers.get(member.getName()) + 1);
					} else {
						selectedComitteeMembers.put(member.getName(), 1);
					}

				}
				Miner member = committeeMembers.get(0);
				if (selectedMiner.containsKey(member.getName())) {
					selectedMiner.put(member.getName(), selectedComitteeMembers.get(member.getName()) + 1);
				} else {
					selectedMiner.put(member.getName(), 1);
				}
			} catch (NoSuchAlgorithmException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}

		System.out.println(finalTimes);
		System.out.println(selectedComitteeMembers);
		System.out.println(selectedMiner);

	}

	/**
	 * Start Consensus algorithm after committee members are elected
	 * 
	 * @param committeeMembers List of all committee members
	 */
	private static void startMining(List<Miner> committeeMembers) {
		Miner selectedMiner = committeeMembers.get(0);
		try {
			selectedMiner.startMining();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Miner> commiteeMemberElection(int typeOfAlgo, Map<String, Integer> priority, int cutOffScore) {
		if (typeOfAlgo == 0) {
			return PreferenceModels.numericalPreference(priority, cutOffScore);

		} else {
			try {
				return Network.getMinerRandom(cutOffScore);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * Call the respective preference model
	 * 
	 * @param typeOfAlgo  type of preference model
	 * @param priority    List of preferred parameters
	 * @param cutOffScore Threshold value
	 * @return
	 */
	public static List<Miner> commiteeMemberElection(int typeOfAlgo, List<String> priority, int cutOffScore) {
		if (typeOfAlgo == 1) {
			return PreferenceModels.prioritizedPreference(priority, cutOffScore);
		} else if (typeOfAlgo == 2) {
			return PreferenceModels.balancedPreference(priority, cutOffScore);

		} else {
			try {
				return Network.getMinerRandom(cutOffScore);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * Distribute trapdoor key to all committee members
	 * 
	 * @param committeeMembers List of committee members
	 * @param trapDoorKeys     Byte of the trapdoor key
	 */
	public static void distributeKeysToCommitteeMembers(List<Miner> committeeMembers,
			ArrayList<ArrayList<Byte>> trapDoorKeys) {
		int i = 0;
		for (Miner miner : committeeMembers) {
			miner.setTrapDoorKey(trapDoorKeys.get(i));
			if (i < trapDoorKeys.size() - 1)
				i += 1;
		}
	}

	/**
	 * Generates transactions
	 */
	public static void generateTransactions() {
		PublicKey pubKey = Network.getNetworkPublicKey();
		PrivateKey privKey = Network.getNetworkPrivateKey();

		Miner trader = new Miner("Trader", 0, 0.0, 0.0, 0.0);

		TxRecord newTransaction = new TxRecord(pubKey, trader.getWallet().publicKey, 1000, null);
		newTransaction.generateSignature(privKey);
		Network.addUncofirmedTxRecord(newTransaction);
	}

}
