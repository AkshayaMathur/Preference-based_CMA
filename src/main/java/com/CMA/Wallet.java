package com.CMA;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;

/**
 * @author akshayamathur
 *
 * Miner Wallet Class
 */
public class Wallet {

    public PublicKey publicKey;
    public PrivateKey privateKey;

    public HashMap<String, TxOutput> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPairValues();
    }

    /**
     * Generate private & public key
     */
    private void generateKeyPairValues() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
