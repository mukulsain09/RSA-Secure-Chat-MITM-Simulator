import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.math.BigInteger;

public class UserA {
    public static void main(String[] args) throws Exception {
        int portNumber = 1234;

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        // Step 1: Generate RSA key pair (at least 6-bit prime numbers p and q)
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Display public key parameters
        System.out.println("User A (Server) - Public Key Modulus (n): " + ((RSAPublicKey) publicKey).getModulus());
        System.out.println("User A (Server) - Public Key Exponent (e): " + ((RSAPublicKey) publicKey).getPublicExponent());

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("User A (Server) is running and waiting for User B (Client) to connect...");

            // Step 2: Accept connection from User B (Client)
            Socket clientSocket = serverSocket.accept();
            System.out.println("User B (Client) connected: " + clientSocket);

            // Step 5: Receive encrypted message from User B (Client)
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            byte[] encryptedMessage = (byte[]) inputStream.readObject();
            System.out.println("User A (Server) - Received encrypted message from User B (Client)");

            // Step 6: Decrypt message using private key
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
            String decryptedText = new String(decryptedMessage);
            System.out.println("User A (Server) - Decrypted Text: " + decryptedText);

            // Close connection
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Helper method to generate RSA key pair
    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(6, random);
        BigInteger q = BigInteger.probablePrime(6, random);

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512, random); // Use a larger key size for better security
        return keyGen.generateKeyPair();
    }
}
