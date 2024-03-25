package com.example.BlockchainServer;

import com.example.BlockchainServer.block.Block;
import com.example.BlockchainServer.block.BlockService;
import com.example.BlockchainServer.decoders.ObjectDecoder;
import com.example.BlockchainServer.encoders.ObjectEncoder;

import com.example.BlockchainServer.message.Message;
import com.example.BlockchainServer.message.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.collection.IList;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(
        value = "/blockchainServer/{username}",
        decoders = {ObjectDecoder.class},
        encoders = {ObjectEncoder.class})
@Component
public class BlockchainServer {
    private Session session;
    private static final Set<BlockchainServer> chatEndpoints = new CopyOnWriteArraySet<>();
    private static final Map<String, Session> onlineSessions = new HashMap<>();
    private static final HashMap<String, String> users = new HashMap<>();

    private static final List<Message> transactionValidationResults = new ArrayList<>();
    private static int transactionValidationCounter = 0;
    private static String transactionSender;

    private static final List<Message> blockValidationResults = new ArrayList<>();
    private static int blockValidationCounter = 0;
    private static String blockSender;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
    private static final IList<Object> IBlockchain = hazelcast.getList("blockchain");

    private static BlockService blockService = null;

    public BlockchainServer() {
    }

    @Autowired
    public BlockchainServer(BlockService blockService) {
        BlockchainServer.blockService = blockService;
    }

    public static void init() {
        Object lastBlock = blockService.getLastBlock();
        if(lastBlock!=null) {
            IBlockchain.add(lastBlock);
        }
        printDashedLine();
        System.out.println("Initial Blockchain: "+ Arrays.toString(IBlockchain.toArray()));
        printDashedLine();
    }

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("username") String userName) throws IOException, EncodeException {

        printDashedLine();
        print(userName+" Connected!");

        this.session = session;
        chatEndpoints.add(this);
        onlineSessions.put(userName, session);
        users.put(session.getId(), userName);

        Message msgToBroadcast = new Message();
        msgToBroadcast.setContent(userName + " Active now!");
        broadcast(msgToBroadcast, session);

        Message msgToSend = new Message();
        msgToSend.setType(MessageType.SET_BLOCKCHAIN);
        msgToSend.setBlockchain(IBlockchain);
        sendObject(userName, msgToSend);
    }


    @OnMessage
    public void onMessage(Session session, Object message) throws IOException, EncodeException {
        String json = objectMapper.writeValueAsString(message);
        Message msgFromClient = objectMapper.readValue(json, Message.class);

        print("Message from "+users.get(session.getId()) + ": " + msgFromClient.getType());

        if (msgFromClient.getType().equals(MessageType.BROADCAST_TRANSACTION)) {

            print("Transaction: "+msgFromClient.getTransaction());

            Message msgToSend = new Message();
            msgToSend.setType(MessageType.VALIDATE_TRANSACTION);
            msgToSend.setTransaction(msgFromClient.getTransaction());

            transactionSender = users.get(session.getId());
            broadcast(msgToSend);
        } else if (msgFromClient.getType().equals(MessageType.TRANSACTION_VALIDATION_RESULT)) {
            transactionValidationResults.add(msgFromClient);

            if (msgFromClient.isValidTransaction()) {
                transactionValidationCounter++;
                print(users.get(session.getId()) + ": ✔ Valid Transaction");
            } else {
                print(users.get(session.getId()) + ": ❌ Invalid Transaction");
            }

            if (transactionValidationResults.size() == onlineSessions.size()) {
                if (transactionValidationCounter == onlineSessions.size()) {
                    String blockMaker = chooseTheBlockMaker(transactionValidationResults);

                    Message msgToSend = new Message();
                    msgToSend.setType(MessageType.MAKE_BLOCK);
                    msgToSend.setTransaction(msgFromClient.getTransaction());

                    sendObject(blockMaker, msgToSend);
                } else {
                    Message msgToSend = new Message();
                    msgToSend.setContent("❌ Invalid transaction");
                    sendObject(transactionSender, msgToSend);
                }

                transactionValidationResults.clear();
                transactionValidationCounter = 0;
            }
        } else if (msgFromClient.getType().equals(MessageType.BROADCAST_BLOCK)) {
            print("Block to broadcast: "+  msgFromClient.getBlock());

            Message msgToSend = new Message();
            msgToSend.setType(MessageType.VALIDATE_BLOCK);
            msgToSend.setBlock(msgFromClient.getBlock());

            blockSender = users.get(session.getId());
            broadcast(msgToSend);
        } else if (msgFromClient.getType().equals(MessageType.BLOCK_VALIDATION_RESULT)) {
            blockValidationResults.add(msgFromClient);

            if (msgFromClient.isValidBlock()) {
                blockValidationCounter++;
                print(users.get(session.getId()) + ": ✔ Valid Block");
            } else {
                print(users.get(session.getId()) + ": ❌ Invalid Block");
            }

            if (blockValidationResults.size() == onlineSessions.size()) {
                if (blockValidationCounter == onlineSessions.size()) {
                    Message msgToSend = new Message();
                    msgToSend.setType(MessageType.RECORD_BLOCK);
                    msgToSend.setBlock(msgFromClient.getBlock());
                    broadcast(msgToSend);

                    recordBlock(msgFromClient.getBlock());

                    print("Updated blockchain: "+Arrays.toString(IBlockchain.toArray()));
                    print("Updated blockchain size: "+IBlockchain.size());
                    printDashedLine();
                } else {
                    Message messageToSend = new Message();
                    messageToSend.setContent("❌ Invalid Block");
                    sendObject(blockSender, messageToSend);
                }

                blockValidationResults.clear();
                blockValidationCounter = 0;
            }
        }

    }

    @OnError
    public void onError(Session session, Throwable throwable) throws EncodeException, IOException {
        System.out.println("There is an error in: " + users.get(session.getId()) + "'s session");
        System.out.println("Error: " + Arrays.toString(throwable.getStackTrace()));
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        chatEndpoints.remove(this);
        onlineSessions.remove(users.get(session.getId()));

        broadcast(users.get(session.getId()) + " Disconnected", session);
    }

    private static void broadcast(Object message, Session session)
            throws IOException, EncodeException {
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                if (endpoint.session != session) {
                    try {
                        endpoint.session.getBasicRemote().sendObject(message);

                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void broadcast(Object message)
            throws IOException, EncodeException {
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {

                try {
                    endpoint.session.getBasicRemote().sendObject(message);

                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void sendObject(String senderUserName, Object message)
            throws IOException, EncodeException {
        Session receiverSession = onlineSessions.get(senderUserName);
        synchronized (receiverSession) {
            try {
                receiverSession.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }

        }
    }

    public String chooseTheBlockMaker(List<Message> messages) {
        String blockMaker = messages.get(0).getUserName();
        for (int i = 1; i < messages.size(); i++) {
            Message currentUser = messages.get(i);
            Message previousUser = messages.get(i - 1);
            blockMaker = ((currentUser.getBalance() > previousUser.getBalance()) ? currentUser.getUserName() : blockMaker);
        }
        return blockMaker;
    }

    private void recordBlock(Object block) throws JsonProcessingException {
        String blockJson = objectMapper.writeValueAsString(block);
        Block blockToSave = objectMapper.readValue(blockJson, Block.class);

        blockService.save(blockToSave);
        IBlockchain.add(block);
    }

    private static int printCounter = 1;
    public void print(String message) {
        System.out.println(printCounter +") "+message);
        printCounter++;
        if(message.startsWith("Updated blockchain size: ")) printCounter=1;
    }

    private static void printDashedLine() {
        System.out.println("\n--------------------------------\n");
    }

}
