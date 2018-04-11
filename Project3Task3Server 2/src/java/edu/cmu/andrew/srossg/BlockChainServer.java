/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.andrew.srossg;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author srossgupta
 */
@WebServlet(name = "BlockChainServer", urlPatterns = {"/BlockChainServer/*"})
public class BlockChainServer extends HttpServlet {

    /**
     * Web service operation
     */
    private BlockChain theBlockChain = null;

    public BlockChainServer() {
        theBlockChain = new BlockChain();
        Block b1 = new Block(0, theBlockChain.getTime(), "{Genesis}", 2);
        theBlockChain.addBlock(b1);
    }

    //Reference: Professor Lab activity codes
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Console: doGET visited");
        String result = "";
        //get the user choice from the client
        String choice = (request.getPathInfo()).substring(1);
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //methods call appropriate to user calls
        if (Integer.valueOf(choice) == 3) {
            result = theBlockChain.toString();
            if (result != null) {
                out.println(result);
                response.setStatus(200);
                //set status if result output is not generated
            } else {
                response.setStatus(401);
                return;
            }
        }
        //verify chain method
        if (Integer.valueOf(choice) == 2) {
            response.setStatus(200);
            boolean validity = theBlockChain.isChainValid();
            out.print("verifying:\nchain verification: ");
            out.println(validity);
        }
    }

    //Reference: Professor Lab activity codes
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        //read the client 
        String XMLdata = br.readLine();
        boolean isSignatureVerified = parseXMLData(XMLdata);
        //set the client status based on the signature verification
        if (isSignatureVerified) {
            response.setStatus(200);
        } else {
            response.setStatus(401);
        }
    }

    //method to verify the user signed message
    private static boolean VerifyInput(String encrypteddata) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //extract the data and signed message
        String[] data = encrypteddata.split("#");
        String clientdataComputedhash = "";
        try {
            clientdataComputedhash = CalHash(data[0].getBytes());
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Unsupported Encoding Exception");
        }
        //compute the hash from the signed message
        byte[] clientHash = data[1].getBytes();
        String clientdecryptHash = RSAdecrypt(clientHash);
        boolean status = false;
        //status if the signature does not matches
        status = clientdecryptHash.equals(clientdataComputedhash);
        return status;
    }

    //calculate the hash value of the transaction
    private static String CalHash(byte[] passSalt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //get the instance value corresponding to Algorithm selected
        MessageDigest key = MessageDigest.getInstance("SHA-256");
        //Reference:https://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
        //update the digest using specified array of bytes
        key.update(passSalt);
        String PassSaltHash = javax.xml.bind.DatatypeConverter.printHexBinary(key.digest());
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

    //parse the XMLdata received from the client to perform the functions 
    private boolean parseXMLData(String XMLData) {
        boolean clientRequestStatus = false;
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
            String Transactiondata = XMLElement.getElementsByTagName("data").item(0).getTextContent();
            String difficulty = XMLElement.getElementsByTagName("difficulty").item(0).getTextContent();
            //call the client function associated with client choice
            clientRequestStatus = AddBlock(Transactiondata, Integer.valueOf(difficulty));
        } catch (SAXException ex) {
            System.out.println("SAXException");
        } catch (IOException ex) {
            System.out.println("IOException");
        } catch (ParserConfigurationException ex) {
            System.out.println("ParserConfigurationException");
        }
        return clientRequestStatus;
    }

    //add block to the blockchain
    private boolean AddBlock(String data, int difficulty) {
        boolean status = false;
        try {
            //verify the client signature
            status = VerifyInput(data);
            if (status) {
                Block b1 = new Block(theBlockChain.getLatestBlock().getIndex() + 1, theBlockChain.getTime(), data, difficulty);
                theBlockChain.addBlock(b1);
            }
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("NoSuchAlgorithmException");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("UnsupportedEncodingException");
        }
        return status;
    }

}
