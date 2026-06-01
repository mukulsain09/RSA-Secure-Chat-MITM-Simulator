import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.math.BigInteger;

public class UserB {
    public static void main(String[] args) throws Exception {
        String serverHostname = "192.168.1.50";
        int portNumber = 1234;

        try (Socket socket = new Socket(serverHostname, 1234)) {
            System.out.println("User B (Client) connected to User A (Server) at " + serverHostname);

            // Step 3: Receive public key from User A (Server)
            ObjectInputStream publicKeyIn = new ObjectInputStream(socket.getInputStream());
            PublicKey publicKey = (PublicKey) publicKeyIn.readObject();
            System.out.println("User B (Client) - Received Public Key from User A (Server)");

            // Step 4: Generate plaintext message
            String plaintext = "Hello, User A!";
            System.out.println("User B (Client) - Plaintext Message: " + plaintext);

            // Step 7: Encrypt message using User A's public key
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedMessage = cipher.doFinal(plaintext.getBytes());
            System.out.println("User B (Client) - Encrypted Text: " + new String(encryptedMessage));

            // Step 8: Send encrypted message to User A (Server)
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(encryptedMessage);
            System.out.println("User B (Client) - Sent encrypted message to User A (Server)");

        } catch (UnknownHostException e) {
            System.err.println("Error: Unknown host " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
