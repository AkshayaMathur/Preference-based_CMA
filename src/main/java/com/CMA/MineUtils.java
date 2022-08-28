package com.CMA;


/**
 * @author akshayamathur
 * 
 * Miner util functions
 */
public class MineUtils {

	
    /**
     * 
     * Mining the block by the Miner
     * 
     * @param block to be validated
     * @param difficulty
     * @return
     */
    public Block mine(Block block, int difficulty) {
        System.out.println("Starting mining:\n");
        int nonce = 0;

        String hash;
        String target = getDificultyString(difficulty);
        if (block.previousBlockHash == null) {
            block.randomness = nonce;
            hash = calculateHash(block);
            block.previousBlockHash = hash;
        }

        while(!block.previousBlockHash.substring( 0, difficulty).equals(target)) {
            nonce++;
            block.randomness = nonce;
            hash = calculateHash(block);
            block.previousBlockHash = hash;
        }

        System.out.println("Block Mined!!!: " + block.previousBlockHash + "  Randomness:" + block.randomness);
        return block;
    }

    /**
     * 
     * Calculate hash value of the block
     * 
     * @param block
     * @return
     */
    private String calculateHash(Block block) {
        return CryptoService.applySha256(
            block.previousBlockHash +
            Long.toString(block.timestamp) +
            block.merkleRoot +
            Double.toString(block.randomness)
        );
    }

    /**
     * 
     * Add difficulty in mining the block
     * 
     * @param difficulty
     * @return
     */
    private String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
}
