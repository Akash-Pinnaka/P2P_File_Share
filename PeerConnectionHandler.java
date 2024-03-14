import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;

/**
 * The PeerConnectionHandler class is responsible for handling the communication
 * with a connected peer, including the handshake protocol and further interactions.
 */
public class PeerConnectionHandler implements Runnable {

    // Reference to the local peer
    Peer self;
    
    // MessageManager for handling messages
    MessageManager messageManager;

    // Socket representing the connection to the client peer
    final Socket clientPeerSocket;

    /**
     * Constructor for PeerConnectionHandler.
     *
     * @param clientPeerSocket The socket representing the connection to the client peer.
     * @param self The local peer.
     * @param messageManager The MessageManager for handling messages.
     */
    public PeerConnectionHandler(Socket clientPeerSocket, Peer self, MessageManager messageManager) {
        this.self = self;
        this.clientPeerSocket = clientPeerSocket;
        this.messageManager = messageManager;
    }

    /**
     * The main execution method for the PeerConnectionHandler.
     * Handles the handshake protocol and further interactions with the connected peer.
     */
    @Override
    public void run() {
        try (clientPeerSocket) {
            
            // Create readers and writers for communication
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientPeerSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientPeerSocket.getOutputStream()));

            // Create input and output streams
            InputStream in = new BufferedInputStream(clientPeerSocket.getInputStream());
            OutputStream out = new BufferedOutputStream(clientPeerSocket.getOutputStream());

            // Read the handshake message from the connected peer
            byte[] response = new byte[32];
            in.read(response);
            ByteBuffer buffer = ByteBuffer.wrap(response);
            Map<String, String> responseToHandshake = messageManager.handshakeMessageManager
                    .readHandshakeMessage(buffer);

            /*
             * Data Logging: a peer establishes a TCP connection to other peer
             */
            System.out.println("[" + new Date(System.currentTimeMillis()) + "]: "
                    + "Peer [" + self.getPeerId() + "] "
                    + "is connected from Peer [" + responseToHandshake.get("peerId") + "].");

            // Send the local peer's handshake message in response
            byte[] handshake = messageManager.handshakeMessageManager.generateHandshakeMessage(self.getPeerId());
            out.write(handshake);
            out.flush();

            /*
             * For the purpose of testing only handshake protocol
             * up to this point, we terminate here.
             * TODO: Implement the protocol beyond handshaking
             */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
