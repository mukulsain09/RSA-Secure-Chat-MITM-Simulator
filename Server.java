import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // Store cracked keys: ClientName -> {d, n}
    private static ConcurrentHashMap<String, BigInteger[]> crackedKeys = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("HACKER SERVER STARTED - Listening on port 1234");
            System.out.println("Waiting for victims to connect...");

            Socket client1Socket = serverSocket.accept();
            System.out.println("Client 1 connected.");
            Socket client2Socket = serverSocket.accept();
            System.out.println("Client 2 connected.");

            PrintWriter out1 = new PrintWriter(client1Socket.getOutputStream(), true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(client1Socket.getInputStream()));
            PrintWriter out2 = new PrintWriter(client2Socket.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(client2Socket.getInputStream()));

            // Intercept Client 1's Key
            BigInteger n1 = new BigInteger(in1.readLine());
            BigInteger e1 = new BigInteger(in1.readLine());
            System.out.println("\n[!] INTERCEPTED KEY FROM CLIENT 1: (e=" + e1 + ", n=" + n1 + ")");
            
            // Background cracking for Client 1
            new Thread(() -> crackKey(n1, e1, "CLIENT 1")).start();

            // Forward to Client 2
            out2.println(n1);
            out2.println(e1);

            // Intercept Client 2's Key
            BigInteger n2 = new BigInteger(in2.readLine());
            BigInteger e2 = new BigInteger(in2.readLine());
            System.out.println("[!] INTERCEPTED KEY FROM CLIENT 2: (e=" + e2 + ", n=" + n2 + ")");
            
            // Background cracking for Client 2
            new Thread(() -> crackKey(n2, e2, "CLIENT 2")).start();

            // Forward to Client 1
            out1.println(n2);
            out1.println(e2);

            out1.println("Connection Secure. Start Chatting.");
            out2.println("Connection Secure. Start Chatting.");

            // Sniffing Loop
            new Thread(new Sniffer(in1, out2, "CLIENT 1")).start();
            new Thread(new Sniffer(in2, out1, "CLIENT 2")).start();
        }
    }

    private static void crackKey(BigInteger n, BigInteger e, String label) {
        System.out.println("[*] Attempting Fermat's Factorization on " + label + "...");
        long start = System.currentTimeMillis();
        
        BigInteger a = sqrt(n).add(BigInteger.ONE);
        BigInteger b2 = a.multiply(a).subtract(n);
        
        while (!isSquare(b2)) {
            a = a.add(BigInteger.ONE);
            b2 = a.multiply(a).subtract(n);
        }
        
        BigInteger b = sqrt(b2);
        BigInteger p = a.subtract(b);
        BigInteger q = a.add(b);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger d = e.modInverse(phi);
        
        // Save the cracked key for decryption
        crackedKeys.put(label, new BigInteger[]{d, n});
        
        long end = System.currentTimeMillis();
        System.out.println("\n[SUCCESS] CRACKED " + label + " PRIVATE KEY!");
        System.out.println("  > d: " + d);
        System.out.println("  > Time taken: " + (end - start) + "ms\n");
    }

    private static BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;
        for (;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2)) return y;
            div2 = div;
            div = y;
        }
    }

    private static boolean isSquare(BigInteger n) {
        BigInteger s = sqrt(n);
        return s.multiply(s).equals(n);
    }

    static class Sniffer implements Runnable {
        private BufferedReader in;
        private PrintWriter out;
        private String name;

        public Sniffer(BufferedReader in, PrintWriter out, String name) {
            this.in = in;
            this.out = out;
            this.name = name;
        }

        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("\n[INTERCEPTED " + name + "]: " + line);
                    
                    // Attempt decryption if key is cracked
                    String targetKeyLabel = name.equals("CLIENT 1") ? "CLIENT 2" : "CLIENT 1";
                    
                    if (crackedKeys.containsKey(targetKeyLabel)) {
                        BigInteger[] keys = crackedKeys.get(targetKeyLabel);
                        String decrypted = RSAUtil.decrypt(line, keys[0], keys[1]);
                        System.out.println("[DECRYPTED MESSAGE]: " + decrypted);
                    } else {
                        System.out.println("[STATUS]: Key for " + targetKeyLabel + " not yet cracked. Cannot decrypt.");
                    }
                    
                    out.println(name + ": " + line);
                }
            } catch (IOException e) {}
        }
    }
}
