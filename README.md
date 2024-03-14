# CNT5106C - P2P File Sharing Software

## Group Name
Akash Pinnaka's Group

## Group Members
1. Akash Pinnaka (UFID: 10094703)
2. Thilak Reddy Kanala (UFID: 88003203)
3. Rida Malik Mubeen (UFID: 12299709)

## How To Use
- Compile all source code: `javac PeerProcess.java`
- Start peers: `java PeerProcess <peerId>`

## Components Implemented

The following components have been implemented for this midpoint checkpoint:

1. **CommonConfigParser Class**

This component is capable of parsing through the Common Configuration file.


2. **PeerInfoConfigParser Class**

This componnent is capable of parsing through the  Peer Info Configuration file.

3. **MessageManager Class**

This component is capable of generating the following messages as a byte stream.
    - Handshake Message
    - Actual Message

4. **PeerProcess Class**

In this component, the **handshake protocol** has been implemented. 

Furthermore, **Data Logger** class is implemented but not used at this point. Logs are displayed in the termnial for the handshaking protocol

## Summary
The following features are functional at this stage:
1. Parsing of the configuration files
2. Message generation (Handshake & Actual Messages)
3. **Handshake Protocol between multiple peers**

