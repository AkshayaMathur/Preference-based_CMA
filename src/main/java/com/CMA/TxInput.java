package com.CMA;


/**
 * @author akshayamathur
 * 
 * It contains the transaction which is still not processed.
 *
 */
public class TxInput {
    /**
     *  Parent transaction id of the transaction
     */
    public String txOutputId;
    /**
     * TxOutput 
     */
    public TxOutput UTXO;

    public TxInput(String txOutputId) {
        this.txOutputId = txOutputId;
    }
}
