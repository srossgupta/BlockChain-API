# BlockChain-API: Custom API to build Blockchain for user transactions 
- Implemented Proof of Work and hashing algorithm for creating a Blockchain  
- Designed the Blockchain API for client server communication based on REST architecture  
- Leveraged symmetric and asymmetric cryptographic algorithms i.e. SHA-256 and RSA to store user transaction securely  

## Task0 
- Create structural block which stores the transaction data with the appropriate fields such as {index,Timestamp, data, previoushash,nonce,difficulty}.Calculate the chainHash of the block and rehash that with previous hash of the block to calculate the Hash.
- Store multiple block on the arraylist and verify the blockchain tampering by checking its validity using chainhash.

## Task1 
- Client program which makes the contact with the SOAP server to interact using Blockchain API. Client also sends sends off the message by signing the Hash using RSA private keys which is further verified by the server.
- It receives the client request and data, check them with public keys. It then further add transaction to the blockchain.


## Task2
- Client program which makes the contact with the SOAP server to interact using Blockchain API.It utilizes single message argument style method to contact with the server.
- Client also sends sends off the message by signing the Hash using RSA private keys which is further verified by the server.
- Contains server program which is the backend for Blockchain API. It receives the client request and data, check them with public keys. It then further add transaction to the blockchain. This implements single message design(SMC)

## Task3
- client program which makes the contact with the REST to interact using Blockchain API.This utilizes doGet and doPost method to contact with the server. Client also sends off the message by signing the Hash using RSA private keys which is further verified by the server.
