package com.CMA;

import java.security.PublicKey;

/**
 * @author akshayamathur
 * Transaction Output
 */
public class TxOutput {
    public String id;
    public PublicKey recipient;
    public float value;
    public String parentTxId;

    public TxOutput(PublicKey recipient, float value, String parentTxId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTxId = parentTxId;
        this.id = CryptoService.applySha256(CryptoService.getStringFromKey(recipient) + value + parentTxId);
    }

    
    /**
     * 
     * Check if the transaction is processed
     * @param publicKey
     * @return
     */
    public boolean isMine(PublicKey publicKey) {
        return (publicKey.equals(recipient));
    }
}
