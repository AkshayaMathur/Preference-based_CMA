package com.CMA;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akshayamathur
 *
 */
public class Miner implements Comparable<Miner> {

	/**
	 * Miner name to identify it Primary key
	 */
	private String name;
	/**
	 * Wallet
	 */
	private Wallet wallet;
	/**
	 * Stake of Miner
	 */
	private Integer stake;
	/**
	 * Processing Power of Miner
	 */
	private Double processingPower;
	/**
	 * Cost of Miner
	 */
	private Double cost;
	/**
	 * Disk Space of Miner
	 */
	private Double diskSpace;
	/**
	 * local chain with the miner
	 */
	private List<Block> localChain;
	/**
	 * Trapdoor key while
	 */
	private List<Byte> trapDoorKey;
	/**
	 * Utils functions for mining the trasnactions
	 */
	private final MineUtils pickaxe;

	public Miner(String name, Integer stake, Double processingPower, Double cost, Double diskSpace) {
		super();
		this.name = name;
		this.wallet = new Wallet();
		this.stake = stake;
		this.processingPower = processingPower;
		this.cost = cost;
		this.diskSpace = diskSpace;
		localChain = Network.getLatestChainFromFakeNetwork();
		pickaxe = new MineUtils();
	}

	public void startMining() throws SQLException {
		System.out.println("START MINING FOR " + this.name);
		List<Block> networkChain;
		networkChain = Network.getLatestChainFromFakeNetwork();
		if (localChain.size() < networkChain.size()) {
			localChain = networkChain;
		}

		Network.adaptDifficulty();

		Block lastBlock = Network.getLastBlockInChain();
		System.out.println(lastBlock.toString());
		if (!lastBlock.isMined()) {

			Double netweorkDifficulty = processingPower / 1000;
			System.out.println(netweorkDifficulty);
			Block solvedBlock = pickaxe.mine(lastBlock, netweorkDifficulty.intValue());

			localChain.get(localChain.size() - 1).transactionIds.forEach(Network::confirmTxRecord);
			localChain.get(localChain.size() - 1).transactionIds = null;
			localChain.get(localChain.size() - 1).randomness = solvedBlock.randomness;
			localChain.get(localChain.size() - 1).previousBlockHash = solvedBlock.previousBlockHash;

			Block block = new Block(localChain.get(localChain.size() - 1).previousBlockHash);
			ArrayList<TxRecord> txRecords = Network.getUnconfirmedTxRecords();
			if (txRecords != null) {
				txRecords.forEach(block::addTransaction);
			}
			block.merkleRoot = CryptoService.getMerkleRoot(block.transactions);
			// transactions need not stay on block. only merkleRoot
			block.transactions = null;

			localChain.add(block);

			networkChain = Network.getLatestChainFromFakeNetwork();
			if (localChain.size() < networkChain.size()) {
				localChain = networkChain;
				System.out.println("My chain is smaller than network chain, switching to that one...\n\n");
			} else {
				Network.sendChainOverFakeNetwork(localChain);
			}
		}
		System.out.println("MINING IS DONE..... ");
	}

	public String getName() {
		return name;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public Integer getStake() {
		return stake;
	}

	public Double getProcessingPower() {
		return processingPower;
	}

	public Double getCost() {
		return cost;
	}

	public Double getDiskSpace() {
		return diskSpace;
	}

	public List<Block> getLocalChain() {
		return localChain;
	}

	public MineUtils getPickaxe() {
		return pickaxe;
	}

	public List<Byte> getTrapDoorKey() {
		return trapDoorKey;
	}

	public void setTrapDoorKey(List<Byte> trapDoorKey) {
		this.trapDoorKey = trapDoorKey;
	}

	@Override
	public String toString() {
		return "Miner [name=" + name + ", wallet=" + wallet + ", stake=" + stake + ", processingPower="
				+ processingPower + ", cost=" + cost + ", diskSpace=" + diskSpace + ", localChain=" + localChain
				+ ", trapDoorKey=" + trapDoorKey + ", pickaxe=" + pickaxe + "]";
	}

	@Override
	public int compareTo(Miner m) {

		return 0;
	}

}
