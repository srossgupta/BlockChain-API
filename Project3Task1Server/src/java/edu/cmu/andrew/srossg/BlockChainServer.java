/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.andrew.srossg;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.jws.*;

/*
 * Author: Sross Gupta Last Modified: Oct 26, 2017 This is the server which is
 * the backend for Blockchain API. I receives the client request and data, check
 * them with public keys. It then further add transaction to the blockchain. 
 * /
 
 /* PS:
 * I don't have to remove the extra padded bit sent from the client end as it is
 * automatically removed when i convert that into the big integer. I have
 * confirmed the logic with the Head TA Jorge regarding the same.
 */
@WebService(serviceName = "BlockChainServer")
public class BlockChainServer {

    private BlockChain theBlockChain = null;

    //constructor to add genesis block to the blockchain
    public BlockChainServer() {
        theBlockChain = new BlockChain();
        Block b1 = new Block(0, theBlockChain.getTime(), "{Genesis}", 2);
        theBlockChain.addBlock(b1);

    }

    //view the transactions on the blockchain
    @WebMethod(operationName = "viewTransactions")
    public String viewTransactions() {
        return theBlockChain.toString();
    }

    //verify the autheticity of the transactions (blocks) on blockchain 
    @WebMethod(operationName = "VerifyTransactions")
    public String VerifyTransactions() {
        return ("verifying\nChain verification: " + theBlockChain.isChainValid());
    }

    //add the transactions with signature on the blockchain after verifying the signature
    @WebMethod(operationName = "addTransaction")
    public String addTransaction(@WebParam(name="data") String data, @WebParam(name="difficulty")int difficulty) {
        String returnStatus="";
        //verifying the signed transaction
        boolean verification = VerifyInput(data);
        if (verification) {
            Block b1 = new Block(theBlockChain.getLatestBlock().getIndex() + 1, theBlockChain.getTime(), data, difficulty);
            theBlockChain.addBlock(b1);
            returnStatus = "Block is added";
        } else {
            returnStatus = "Block cannot be verified";
        }
        return returnStatus;
    }

    //method to verify the user signed message
    private static boolean VerifyInput(String encrypteddata)  {
        //extract the data and signed message
        String[] data = encrypteddata.split("#");
        String clientdataComputedhash = "";
        clientdataComputedhash = CalHash(data[0].getBytes());
        //compute the hash from the signed message
        byte[] clientHash = data[1].getBytes();
        String clientdecryptHash = RSAdecrypt(clientHash);
        boolean status = false;
        //status if the signature does not matches
        status = clientdecryptHash.equals(clientdataComputedhash);
        return status;
    }

    //calculate the hash value of the transaction
    private static String CalHash(byte[] passSalt)  {
        String PassSaltHash="";
        try {
            //get the instance value corresponding to Algorithm selected
            MessageDigest key = MessageDigest.getInstance("SHA-256");
            //Reference:https://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
            //update the digest using specified array of bytes
            key.update(passSalt);
            PassSaltHash = javax.xml.bind.DatatypeConverter.printHexBinary(key.digest());
            
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("NoSuchAlgorithmException");
        }
        return PassSaltHash;
    }

    //decrypt the signature received from the client to compute the hash
    private static String RSAdecrypt(byte[] signedhash) {
        String ab = new String(signedhash);
        //private keys
        BigInteger e = new BigInteger("65537");
        BigInteger n = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
        BigInteger m = new BigInteger(ab);
        BigInteger dataHash = m.modPow(e, n);
        String ComputeddataHash = new String(dataHash.toByteArray());
        return ComputeddataHash;
    }

}
