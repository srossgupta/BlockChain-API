/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task3client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Author: Sross Gupta Last Modified: Oct 26, 2017 This is client program which
 * makes the contact with the REST to interact using Blockchain API.This
 * utilizes doGet and doPost method to contact with the server. Client also
 * sends sends off the message by signing the Hash using RSA private keys which
 * is further verified by the server.
 */
public class Project3Task3Client {

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        int choice;

        do {
            Scanner in = new Scanner(System.in);
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Exit");

            //getting the correct choice
            do {
                choice = in.nextInt();
                in.nextLine();

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
                    signeddata = data + "#" + CalHash(data.getBytes());
                    String toSenddata = createXMLString(choice, signeddata, difficulty);
                    doPost(toSenddata);
                    break;
                //verify the transaction
                case 2:
                    doGet(2);
                    break;
                //view the transaction    
                case 3:
                    doGet(3);
                    break;
                default:
                    break;
            }

        } while (choice < 4);
    }

    //creating the XML file to be sent to the client
    public static String createXMLString(int choice, String Signeddata, int difficulty) {
        StringBuilder XML = new StringBuilder();
        XML.append("<?xml version = \"1.0\"?>");
        XML.append("<blockChainMessage>");
        XML.append("<operation>").append(choice).append("</operation>");
        XML.append("<data>").append(Signeddata).append("</data>");
        XML.append("<difficulty>").append(difficulty).append("</difficulty>");
        XML.append("</blockChainMessage>");
        return (XML.toString());
    }

    //calculating the hash value of the message
    public static String CalHash(byte[] data) {
        String Signeddata = "";
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

    public static void doGet(int choice) {

        // Make an HTTP GET passing the name on the URL line
        HttpURLConnection conn;
        int status = 0;

        try {

            // pass the name on the URL line
            URL url = new URL("http://localhost:10090/Project3Task3Server/BlockChainServer" + "/" + choice);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");
            // wait for response
            status = conn.getResponseCode();

            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                String msg = conn.getResponseMessage();
                System.out.println("Error message:" + msg);
                System.out.println("Error:" + status);
            } else {
                String output = "";
                // things went well so let's read the response
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                //get data from the server
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    public static int doPost(String Blockdata) {

        int status = 0;

        try {
            // Make call to a particular URL
            URL url = new URL("http://localhost:10090/Project3Task3Server/BlockChainServer/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to POST and send name value pair
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            // write to POST data area
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(Blockdata);
            out.close();

            // get HTTP response code sent by server
            status = conn.getResponseCode();
            if (status == 200) {
                System.out.println("Block Added");
            } else {
                System.out.println("Block cannot be verified");
            }

            //close the connection
            conn.disconnect();
        } // handle exceptions
        catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
        } catch (IOException e) {
            System.out.println("IOException");
        }

        // return HTTP status
        return status;
    }

}
