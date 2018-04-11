/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task1client;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Author: Sross Gupta Last Modified: Oct 26, 2017 This is client program which
 * makes the contact with the SOAP server to interact using Blockchain API.
 * Client also sends sends off the message by signing the Hash using RSA private
 * keys which is further verified by the server.
 */


//calculate time
public class Project3Task1Client {

    public static void main(String[] args) {
        int choice;
        //catching the exceptions
        
            //client interface
            do {
                Scanner in = new Scanner(System.in);
                System.out.println("1. Add a transaction to the blockchain.");
                System.out.println("2. Verify the blockchain.");
                System.out.println("3. View the blockchain.");
                System.out.println("4. Exit");

                do {
                    choice = in.nextInt();
                    in.nextLine();
                    //enter the correct choice input
                    if (choice > 4) {
                        System.out.println("Enter the correct choice");
                    }
                } while (choice > 4);

                switch (choice) {
                    //add the transaction
                    case 1:
                        System.out.println("Enter difficulty:");
                        int difficulty = in.nextInt();
                        in.nextLine();
                        System.out.println("Enter the data:");
                        String data = in.nextLine();
                        String signeddata = "";
                        signeddata = CalHash(data.getBytes());
                        String final_out_data = data + "#" + signeddata;
                        String output = addTransaction(final_out_data, difficulty);
                        System.out.println(output);
                        break;
                    //verify the blockchain    
                    case 2:
                        System.out.println(verifyTransactions());
                        break;
                    //view the blockchain    
                    case 3:
                        System.out.println(viewTransactions());
                        break;
                    default:
                        break;
                }
            } while (choice < 4);
            //catching the server API exceptions    
        
    }

    //calculating the hash value of the message
    public static String CalHash(byte[] data)  {
        String Signeddata="";
        try {
            //get the instance value corresponding to Algorithm selected
            MessageDigest key = MessageDigest.getInstance("SHA-256");
            //update the digest using specified array of bytes
            key.update(data);
            String dataHash = javax.xml.bind.DatatypeConverter.printHexBinary(key.digest());
            //sign the hash value using RSA keys
            Signeddata = RSAEncrypt(dataHash.getBytes());
            
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("NoSuchAlgorithmException");
        }
        return Signeddata;
    }

    //signing off the message
    public static String RSAEncrypt(byte[] signedData) {
        //sign the message using the private keys
        BigInteger d = new BigInteger("339177647280468990599683753475404338964037287357290649639740920420195763493261892674937712727426153831055473238029100340967145378283022484846784794546119352371446685199413453480215164979267671668216248690393620864946715883011485526549108913");
        BigInteger n = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
        BigInteger e = new BigInteger("65537");

        //creating the new byte array to pad that with 0 in order to avoid -ve byte signing off by RSA
        byte[] changesignedDataHash = new byte[signedData.length + 1];
        for (int i = 1; i < signedData.length + 1; i++) {
            changesignedDataHash[i] = signedData[i - 1];
        }
        //sign the changed message by padding 0
        BigInteger m = new BigInteger(changesignedDataHash);
        BigInteger key = m.modPow(d, n);
        //return the signed message
        String returnRSAencrypt = new String(key.toString());
        return returnRSAencrypt;
    }

    //methods of server API
     //add the transaction
    private static String addTransaction(java.lang.String data, int difficulty) {
        edu.cmu.andrew.srossg.BlockChainServer_Service service = new edu.cmu.andrew.srossg.BlockChainServer_Service();
        edu.cmu.andrew.srossg.BlockChainServer port = service.getBlockChainServerPort();
        return port.addTransaction(data, difficulty);
    }

        //verify the transaction
    private static String verifyTransactions() {
        edu.cmu.andrew.srossg.BlockChainServer_Service service = new edu.cmu.andrew.srossg.BlockChainServer_Service();
        edu.cmu.andrew.srossg.BlockChainServer port = service.getBlockChainServerPort();
        return port.verifyTransactions();
    }

    
    //view the transaction
    private static String viewTransactions() {
        edu.cmu.andrew.srossg.BlockChainServer_Service service = new edu.cmu.andrew.srossg.BlockChainServer_Service();
        edu.cmu.andrew.srossg.BlockChainServer port = service.getBlockChainServerPort();
        return port.viewTransactions();
    }

}
