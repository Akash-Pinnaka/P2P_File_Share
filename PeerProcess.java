import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;

/**
 * The PeerProcess class represents the main entry point for a peer in the P2P file-sharing system.
 */
public class PeerProcess {

    // Configuration and information parsers
    CommonConfigParser commonConfigInfo;
    PeerInfoConfigParser peerConfigInfo;
    
    // Message manager for handling communication messages
    MessageManager messageManager;
    
    // Data logger for logging peer activities
    DataLogger logger;

    // Self peer information
    Peer self;

    /**
     * Constructor for PeerProcess.
     *
     * @param peerID The peer ID of the current peer.
     */
    public PeerProcess(String peerID) {
        commonConfigInfo = new CommonConfigParser("Common.cfg");
        peerConfigInfo = new PeerInfoConfigParser("PeerInfo.cfg");
        messageManager = new MessageManager();
        this.self = peerConfigInfo.peerMap.get(peerID);
    }

    /**
     * Assigns a data logger to the current peer for logging activities.
     *
     * @param peerId The peer ID for which to assign the data logger.
     */
    public void assignDataLogger(String peerId) {
        this.logger = new DataLogger(peerId);
    }

    /**
     * Starts the server port to listen for incoming connections from other peers.
     */
    void startServerPort() {
        try (ServerSocket server = new ServerSocket(self.getListeningPort())) {
            while (true) {
                try {
                    Socket socket = server.accept();
                    Thread task = new Thread(new PeerConnectionHandler(socket, self, messageManager));
                    task.start();
                } catch (IOException ex) {
                }
            }
        } catch (IOException ex) {
            System.err.println("Couldn't start server: " + ex);
        }
    }

    /**
     * Starts connections with other peers in the network.
     */
    void startConnectionWithOtherPeers() {
        for (Peer peer : peerConfigInfo.peers) {
            if (peer.getPeerId() == self.getPeerId()) {
                break; // Stop making connections when the current peer ID is reached
            }

            // Data Logging: a peer establishes a TCP connection to another peer
            System.out.println("[" + new Date(System.currentTimeMillis()) + "]: "
                    + "Peer [" + self.getPeerId() + "] "
                    + "makes a connection to Peer [" + peer.getPeerId() + "].");

            try (Socket socket = new Socket(peer.getHostName(), peer.getListeningPort())) {
                InputStream in = new BufferedInputStream(socket.getInputStream());
                OutputStream out = new BufferedOutputStream(socket.getOutputStream());

                byte[] handshake = messageManager.handshakeMessageManager.generateHandshakeMessage(self.getPeerId());
                out.write(handshake);
                out.flush();

                byte[] response = new byte[32];
                in.read(response);

                ByteBuffer buffer = ByteBuffer.wrap(response);

                Map<String, String> responseToHandshake = messageManager.handshakeMessageManager.readHandshakeMessage(buffer);

                // Validate handshake correctness
                byte[] tempZeroBits = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                String tempZeroBitsString = new String(tempZeroBits);

                if (!(responseToHandshake.get("handshakeHeader").equals("P2PFILESHARINGPROJ"))
                        || !(responseToHandshake.get("zeroBits").equals(tempZeroBitsString))
                        || !(responseToHandshake.get("peerId").equals(peer.getPeerId()))) {
                    // Incorrect Handshake, break the loop
                    break;
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The main method for the PeerProcess class.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        String inputPeerID = args[0];

        PeerProcess peer = new PeerProcess(inputPeerID);
        peer.assignDataLogger(inputPeerID);

        peer.startConnectionWithOtherPeers();
        peer.startServerPort();
    }
}
