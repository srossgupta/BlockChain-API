/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.andrew.srossg;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.jws.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * Author: Sross Gupta Last Modified: Oct 26, 2017 This is the server which is
 * the backend for Blockchain API. I receives the client request and data, check
 * them with public keys. It then further add transaction to the blockchain. This implements single message design.
 * /
 
 /* PS:
 * I don't have to remove the extra padded bit sent from the client end as it is
 * automatically removed when i convert that into the big integer. I have
 * confirmed the logic with the Head TA Jorge regarding the same.
 */
@WebService(serviceName = "BlockChainServer")
public class BlockChainServer {

    /**
     * Web service operation
     */
    private BlockChain theBlockChain = null;

    //constructor to add genesis block to the blockchain
    public BlockChainServer()  {
        theBlockChain = new BlockChain();
        Block b1 = new Block(0, theBlockChain.getTime(), "{Genesis}", 2);
        theBlockChain.addBlock(b1);
    }

    //method to client to get the data from the client
    @WebMethod(operationName = "ContactServer")
    public String clientXMLData(@WebParam(name="XMLdata")String clientdata) {
        return (parseXMLData(clientdata));
    }

    //parse the XMLdata received from the client to perform the functions 
    private String parseXMLData(String XMLData) {
        String clientRequestStatus = "";
        try {

            //Reference:https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
            //creating the document builder and factory object to parse the xml data
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document blockchainMessage = null;
            InputSource ifs = new InputSource(new StringReader(XMLData));
            //parse the XML message
            blockchainMessage = builder.parse(ifs);
            blockchainMessage.getDocumentElement().normalize();
            //get the message by the tag names in XML data sent
            NodeList XMLList = blockchainMessage.getElementsByTagName("blockChainMessage");
            //get the node (single) from the send data
            Node XMLNode = XMLList.item(0);
            //extract the elements associated with the node 0 
            Element XMLElement = (Element) XMLNode;
            String clientchoice = XMLElement.getElementsByTagName("operation").item(0).getTextContent();
            String Transactiondata = XMLElement.getElementsByTagName("data").item(0).getTextContent();
            String difficulty = XMLElement.getElementsByTagName("difficulty").item(0).getTextContent();
            //call the client function associated with client choice
            clientRequestStatus = callfunctions(Integer.valueOf(clientchoice), Transactiondata, Integer.valueOf(difficulty));
        } catch (SAXException ex) {
            System.out.println("SAXException");
        } catch (IOException ex) {
            System.out.println("IOException");
        } catch (ParserConfigurationException ex) {
            System.out.println("ParserConfigurationException");
        }
        return clientRequestStatus;
    }

    //calls appropriate method as per the client choice sent
    private String callfunctions(int choice, String data, int difficulty) {
        String blockChaininfo = "";
        switch (choice) {
            //add transaction
            case 1:
                boolean status = false;
                //verify the signature sent by the client
                status = VerifyInput(data);
                if (status) {
                    Block b1 = new Block(theBlockChain.getLatestBlock().getIndex() + 1, theBlockChain.getTime(), data, difficulty);
                    theBlockChain.addBlock(b1);
                    blockChaininfo = "Block Added";
                } else {
                    blockChaininfo = "Block cannot be verified";
                }
                break;
                //verify the block chain
            case 2:
                blockChaininfo = "Verifying\nChain Verification: " + theBlockChain.isChainValid();
                break;
                //view the blockchain
            case 3:
                blockChaininfo = theBlockChain.toString();
                break;
            default:
                break;
        }
        return blockChaininfo;
    }

    //method to verify the user signed message
    private static boolean VerifyInput(String encrypteddata){
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
    private static String CalHash(byte[] passSalt) {
        String PassSaltHash="";
        try {
            //get the instance value corresponding to Algorithm selected
            MessageDigest key = MessageDigest.getInstance("SHA-256");
            //Reference:https://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
            //update the digest using specified array of bytes
            key.update(passSalt);
            PassSaltHash = javax.xml.bind.DatatypeConverter.printHexBinary(key.digest());
            return PassSaltHash;
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
