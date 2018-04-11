/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.andrew.srossg;

/*
 * Author: Sross Gupta Last Modified: Oct 26, 2017 In this program we build a
 * Block and Blockchain system to hold the transaction data on the block. This
 * class implements a block of the blockchain. A block stores the transaction
 * data with the appropriate fields such as {index,Timestamp, data,
 * previoushash,nonce,difficulty}. You calculate the chainHash of the block and
 * rehash that with previous hash of the block to calculate the Hash.
 */
//import the relevant packages
import java.io.*;
import java.math.*;
import java.security.*;
import java.sql.Timestamp;

public class Block {

    //class variables of the block (transaction)
    String previousHash = "";
    Timestamp timestamp = null;
    int difficulty;
    BigInteger nonce = new BigInteger("0");
    int index = 0;
    String data;

    //Constructor to initialize the values of the block
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;

    }

    //getters to get the classvariables
    public String getPreviousHash() {
        return previousHash;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public int getIndex() {
        return index;
    }

    public String getData() {
        return data;
    }

    //setters to set the classvariables
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setData(String data) {
        this.data = data;
    }

    //print the block information
    @Override
    public String toString() {
        String block = "[" + Integer.toString(index) + "," + timestamp
                + "," + data + "," + previousHash + "," + nonce.toString()
                + "," + Integer.toString(difficulty) + "]";
        return block;
    }

    //create the hash of the method and returning the hex value of it
    public String calculateHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //get the instance value corresponding to Algorithm selected
        String concat = Integer.toString(index) + timestamp + data.split("#")[0] + previousHash + nonce.toString()
                + Integer.toString(difficulty);
        MessageDigest key = MessageDigest.getInstance("SHA-256");
        //update the digest using specified array of bytes
        key.update(concat.getBytes());
        String concatHash = javax.xml.bind.DatatypeConverter.printHexBinary(key.digest());
        return concatHash;
    }

    //check if the hash created is of appropriate difficulty or not
    public String proofOfWork(int difficult) {
        StringBuilder match = new StringBuilder("");
        boolean check = true;
        String hash = "";

        //create a string of 0's as per the difficulty
        for (int i = 0; i < difficult; i++) {
            match.append("0");
        }

        //catching the exceptions
        try {
            //creating the hash of appropriate difficulty till it matches difficulty
            do {

                hash = calculateHash();
                String sub = (hash.substring(0, difficult));
                if (sub.equals(match.toString())) {
                    check = false;
                } else {
                    //incrementing the nonce
                    nonce = nonce.add(BigInteger.ONE);
                    check = true;
                }
            } while (check);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("NoSuchAlgorithmException");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("UnsupportedEncodingException");
        }

        return (hash);
    }

}

