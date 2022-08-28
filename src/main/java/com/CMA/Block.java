package com.CMA;

import java.util.ArrayList;
import java.util.Date;

public class Block {

	/**
	 * Previous Hash
	 */
	public String previousBlockHash;
	/**
	 * Transactions Informations
	 */
	public ArrayList<TxRecord> transactions = new ArrayList<>();
	public ArrayList<String> transactionIds = new ArrayList<>();
	public String merkleRoot = "emptyString";
	/**
	 * Randomness
	 */
	public Integer randomness;
	/**
	 * Next block Hash
	 */
	public String hashNext;
	/**
	 * Timestamp
	 */
	public long timestamp;

	//
	/**
	 * 
	 * Constructor
	 * 
	 * @param previousBlockHash Previous block hash
	 */
	public Block(String previousBlockHash) {
		this.previousBlockHash = previousBlockHash;
		this.timestamp = new Date().getTime();
	}

	//
	/**
	 * 
	 * Check if the block is mined
	 * 
	 * @return true/false
	 */
	public boolean isMined() {
		return previousBlockHash != null;
	}

	//
	/**
	 * 
	 * Add transactions to the block
	 * 
	 * @param transaction Transaction record to be added to block
	 * @return true/false
	 */
	public boolean addTransaction(TxRecord transaction) {
		if (transaction == null)
			return false;
		if ((!transaction.processTx())) {
			System.out.println("Transaction failed to process. Discarded.");
			return false;
		}

		transactions.add(transaction);
		transactionIds.add(transaction.txId);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}

	@Override
	public String toString() {
		return "Block [" + "previousBlockHash=" + previousBlockHash + ", merkleRoot=" + merkleRoot + ", transactions="
				+ transactions + ", transactionIds=" + transactionIds + ", randomness=" + randomness + ", hashNext="
				+ hashNext + ", timestamp=" + timestamp + "]";
	}

}
