package com.CMA;

import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author akshayamathur
 *
 *         Contains all the utility functions for performing cryptographic
 *         functions
 */
public class CryptoService {

	/**
	 * 
	 * Applies Sha256 to a string
	 * 
	 * @param input a string
	 * @return the result a string
	 */
	public static String applySha256(String input) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Applies sha256 to our input,
			byte[] hash = digest.digest(input.getBytes("UTF-8"));

			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * Applies ECDSA Signature and returns the result ( as bytes ).
	 * 
	 * @param privateKey Private key to which ECDSA Signature has to be applied
	 * @param input
	 * @return the result a bytes array.
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	/**
	 * 
	 * Verifies a String signature
	 * 
	 * @param publicKey
	 * @param data
	 * @param signature
	 * @return ture/false
	 */
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * Convert Key to string
	 * 
	 * @param key
	 * @return a string version of the key
	 */
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * 
	 * Convert transaction to a string to get merkle root
	 * 
	 * @param transactions
	 * @return
	 */
	public static String getMerkleRoot(ArrayList<TxRecord> transactions) {
		int count = transactions.size();

		List<String> previousTreeLayer = new ArrayList<String>();
		for (TxRecord transaction : transactions) {
			previousTreeLayer.add(transaction.txId);
		}
		List<String> treeLayer = previousTreeLayer;

		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i += 2) {
				treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}

		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

	/**
	 * 
	 * Convert String to Public key
	 * 
	 * @param publicKey
	 * @return a PublicKey
	 */
	public static PublicKey getPublicKeyFromString(String publicKey) {
		try {
			EncodedKeySpec publicKeySpace = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
			KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
			return keyFactory.generatePublic(publicKeySpace);
		} catch (Exception e) {
			System.out.println("ERR in getting PublicK from DB string");
		}

		return null;
	}

	/**
	 * 
	 * Convert String to Private key
	 * 
	 * @param privateKey
	 * @return a PrivateKey
	 */
	public static PrivateKey getPrivateKeyFromString(String privateKey) {
		try {
			EncodedKeySpec privateKeySpace = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
			KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
			return keyFactory.generatePrivate(privateKeySpace);
		} catch (Exception e) {
			System.out.println("ERR in getting PrivateK from DB string");
		}

		return null;
	}

	/**
	 * 
	 * Generate trapdoor keys
	 * 
	 * @param noOfCommitteeMember
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static ArrayList<ArrayList<Byte>> generateTrapdoorKeys(int noOfCommitteeMember)
			throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		Key privateKey = kp.getPrivate();

		byte[] encodedPrivateKey = privateKey.getEncoded();

		int divLen = noOfCommitteeMember;
		if (encodedPrivateKey.length < noOfCommitteeMember) {
			divLen = encodedPrivateKey.length;

		}
		return splitKeyIntoEqualParts(encodedPrivateKey, divLen);
	}

	/**
	 * 
	 * Split key into equal parts
	 * 
	 * @param key
	 * @param divLen
	 * @return
	 */
	public static ArrayList<ArrayList<Byte>> splitKeyIntoEqualParts(byte[] key, int divLen) {
		int i = 0;
		final int NG = (key.length + divLen - 1) / divLen;
		ArrayList<ArrayList<Byte>> ans = new ArrayList<>();
		while (i < key.length) {
			ArrayList<Byte> temp = new ArrayList<>();
			for (int j = i; j < i + NG && j < key.length; j++) {
				temp.add(key[j]);
			}
			ans.add(temp);
			i = i + NG;

		}
		return ans;
	}
}
