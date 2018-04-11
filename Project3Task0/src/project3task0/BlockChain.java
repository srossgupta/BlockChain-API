/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task0;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

/*
 * Author: Sross Gupta Last Modified: Oct 26, 2017 In this program we build a
 * Blockchain system to hold the multiple block on the arraylist.This also
 * verifies the if the blockchain tampering by checking its validity.
 */
public class BlockChain {

    List<Block> chain ;
    String chainHash;

    public BlockChain(){
        chainHash="";
        chain = new ArrayList<>();
    }

    public static void main(String[] args) {
        BlockChain bc = new BlockChain();
        int choice;
        int index = 0;
        // adding the genesis block to the blockchain
        Block genesis = new Block(index, bc.getTime(), "{Genesis}", 2);
        bc.addBlock(genesis);
        // User interface, keep on repeating till the user exits
        do {
            Scanner in = new Scanner(System.in);
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Exit");

            //check if the user choice is correct or not
            do {
                choice = in.nextInt();
                in.nextLine();

                if (choice > 4) {
                    System.out.println("Enter the correct choice");
                }
            } while (choice > 4);

            switch (choice) {
                //add transaction
                case 1:
                    index++;
                    System.out.println("Enter difficulty:");
                    int difficulty = in.nextInt();
                    in.nextLine();
                    System.out.println("Enter the data:");
                    String data = in.nextLine();
                    // add the transaction to the blockchain
                    //calculate time to add block for appropriate difficulty (Commented the codes)
//                    long start = System.currentTimeMillis();
                    Block addTransaction = new Block(index, bc.getTime(), data, difficulty);
                    bc.addBlock(addTransaction);
//                    long stop = System.currentTimeMillis();
//                    System.out.println("Time:"+(stop-start));
                    break;
                //Verifying the blockchain
                case 2:
                    //calculate time to verify the Blockchain
//                    long start = System.currentTimeMillis();
                    System.out.println("Verifying");
                    System.out.println("Chain Verification: " + bc.isChainValid());
//                    long stop = System.currentTimeMillis();
//                    System.out.println("Time:" + (stop - start));
                    break;
                //printing all the blocks in the blockchain
                case 3:
                    System.out.println(bc.toString());
                    break;
                default:
                    break;
            }
        } while (choice < 4);
    }

    //adding the block to the blockchain
    public void addBlock(Block newBlock) {
        //setting the previous hash of the block of the chainhash computed of the previous transaction
        newBlock.setPreviousHash(chainHash);
        //computing the chainhash by checking the difficulty
        chainHash = newBlock.proofOfWork(newBlock.difficulty);
        chain.add(newBlock);

    }

    //get the latest block in the blockchain
    public Block getLatestBlock() {
        int size = chain.size();
        Block LatestBlock = chain.get(size - 1);
        return LatestBlock;
    }

    //Method to print the whole blockchain
    @Override
    public String toString() {
        int size = chain.size();
        StringBuilder sb = new StringBuilder("");
        sb.append("View the Blockchain\n");
        sb.append("Chain : {");
        //loop to add all the block details in the blockchain
        for (int i = 0; i < size; i++) {
            sb.append(chain.get(i).toString());
            sb.append("\n");
        }
        sb.append("}\n");
        sb.append("chainHash == ");
        //appending the chainhash of the whole Blockchain
        sb.append(chainHash);
        return sb.toString();
    }

    //validating the blockchain
    public boolean isChainValid() {
        boolean valid = false;
        int size = chain.size();
        //if there is only one block in the blockchain
        if (size == 1) {
            try {
                int difficultyCheck = chain.get(size - 1).difficulty;
                String checkHash = chain.get(size - 1).calculateHash();
                StringBuilder strCheck = new StringBuilder("");
                for (int i = 0; i < difficultyCheck; i++) {
                    strCheck.append("0");
                }
                // checking the proofofwork of the each block
                if ((checkHash.substring(0, difficultyCheck)).equals(strCheck.toString())) {
                    valid = checkHash.equals(chainHash);
                } else {
                    valid = false;
                }
            } catch (NoSuchAlgorithmException ex) {
                System.out.println("NoSuchAlgorithmException");
            } catch (UnsupportedEncodingException ex) {
                System.out.println("UnsupportedEncodingException");
            }

            //if the there are mutiple blocks on the blockchain
        } else {
            //looping through all the blocks in the bloackchain
            for (int i = 1; i < size; i++) {
                try {
                    //calculating difficulty and hash of the i block
                    int difficultyCheck1 = chain.get(i - 1).difficulty;
                    String checkHash_b1 = chain.get(i - 1).calculateHash();
                    //calculating previoushash of i+1 block
                    String checkHash_b2 = chain.get(i).previousHash;
                    //to verify hash of i block should be equal to previoushash of i+1 block
                    if (checkHash_b1.equals(checkHash_b2)) {
                        StringBuilder strCheck1 = new StringBuilder("");

                        //checking the proof of work of the blocks
                        for (int j = 0; j < difficultyCheck1; j++) {
                            strCheck1.append("0");
                        }

                        //performing proof of work for each block
                        if (checkHash_b1.substring(0, difficultyCheck1).equals(strCheck1.toString())) {
                            valid = true;
                        } else {
                            valid = false;
                            break;
                        }
                    } else {
                        valid = false;
                        break;
                    }

                    //validating the last block in the blockchain
                    if (i == size - 1) {
                        //performing proofofwork for the last block
                        int difficultyCheck2 = chain.get(i).difficulty;
                        StringBuilder strCheck2 = new StringBuilder("");
                        for (int j = 0; j < difficultyCheck2; j++) {
                            strCheck2.append("0");
                        }
                        //proof of work for hash for last block in the hash
                        String checkHash_lastBlock = chain.get(i).calculateHash();
                        if (checkHash_lastBlock.substring(0, difficultyCheck2).equals(strCheck2.toString())) {
                            valid = true;
                        } else {
                            valid = false;
                            break;
                        }
                    }

                } catch (NoSuchAlgorithmException ex) {
                    System.out.println("NoSuchAlgorithmException");
                } catch (UnsupportedEncodingException ex) {
                    System.out.println("UnsupportedEncodingException");
                }
            }
        }
        return valid;
    }

    //Get the timestamp for the current time
    public Timestamp getTime() {
        return (new Timestamp(System.currentTimeMillis()));
    }
}
//Time taken to add block for difficulty 5 was 6227 milliseconds
//Time taken to add block for difficulty 4 was 312 milliseconds
//Time taken to verify the chain was 1 milliseconds
