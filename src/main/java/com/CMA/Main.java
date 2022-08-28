package com.CMA;

import java.io.File;
import java.security.Security;
import java.sql.SQLException;

public class Main {

	public static void main(String[] args) throws SQLException {

		Network network;

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		System.out.println("Starting the network!");

		File f = new File(System.getProperty("user.dir") + "/localchain.db");

		// If network is not created, create & start it
		if (!f.exists()) {
			network = new Network();
			network.startNetwork();
		}

		// 100 Create Miners
		for (int i = 1; i <= 100; i++) {
			Miner miner = new Miner("Miner_" + Integer.toString(i), i, (double) ((i * 10) + i), (double) i,
					(double) (i * 10));
			System.out.println("Adding: " + miner.toString());
			Network.addMinerToDb(miner, i);

		}
		System.out.println("ADDED ALL MINERS");

		// Execute CMA Consensus algorithm
		CMAConsensus.executeCMAAlgo();

		f.delete();
	}
}
