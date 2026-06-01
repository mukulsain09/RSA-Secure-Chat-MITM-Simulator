import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.math.BigInteger;

public class ChatClient4 {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextArea securityArea;
    private JTextField messageField;
    private PrintWriter out;
    private BufferedReader in;

    private BigInteger myN, myE, myD;
    private BigInteger peerN, peerE;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClient4 client = new ChatClient4();
            client.createAndShowGUI();
            new Thread(() -> {
                try {
                    client.connect();
                } catch (Exception e) {
                    client.logSecurity("Connection Error: " + e.getMessage());
                }
            }).start();
        });
    }

    public void connect() throws Exception {
        logSecurity("Connecting to server at localhost:1234...");
        Socket socket = new Socket("localhost", 1234);
        logSecurity("Connected!");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Generate RSA Keys
        BigInteger p = BigInteger.valueOf(79);
        BigInteger q = BigInteger.valueOf(67);
        myN = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        myE = BigInteger.valueOf(7);
        myD = myE.modInverse(phi);

        logSecurity("My Public Key: (e=" + myE + ", n=" + myN + ")");

        logSecurity("Waiting for peer key...");
        peerN = new BigInteger(in.readLine());
        peerE = new BigInteger(in.readLine());
        logSecurity("Received Peer Key: (e=" + peerE + ", n=" + peerN + ")");

        // Send my key
        out.println(myN);
        out.println(myE);

        String serverMsg = in.readLine();
        SwingUtilities.invokeLater(() -> chatArea.append("SYSTEM: " + serverMsg + "\n"));

        new Thread(new IncomingReader()).start();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Secure RSA Chat - User 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // Chat Area with modern styling
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(new Color(250, 250, 250));
        chatArea.setMargin(new Insets(10, 10, 10, 10));

        // Security Console with terminal look
        securityArea = new JTextArea();
        securityArea.setEditable(false);
        securityArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        securityArea.setBackground(new Color(30, 30, 30));
        securityArea.setForeground(new Color(0, 255, 65)); // Matrix Green
        securityArea.setMargin(new Insets(10, 10, 10, 10));

        // Panels
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton sendButton = new JButton("Send Securely");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                new JScrollPane(chatArea), new JScrollPane(securityArea));
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.7);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Events
        ActionListener sendAction = e -> sendMessage();
        messageField.addActionListener(sendAction);
        sendButton.addActionListener(sendAction);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void logSecurity(String msg) {
        SwingUtilities.invokeLater(() -> {
            securityArea.append("[" + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + msg + "\n");
            securityArea.setCaretPosition(securityArea.getDocument().getLength());
        });
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            if (peerE == null || peerN == null) {
                chatArea.append("SYSTEM: Error - Public key not yet received!\n");
                return;
            }
            try {
                String encrypted = RSAUtil.encrypt(message, peerE, peerN);
                out.println(encrypted);
                chatArea.append("ME: " + message + "\n");
                logSecurity("CRYPTO_OUT: " + encrypted);
                messageField.setText("");
            } catch (Exception e) {
                logSecurity("ENCRYPTION_ERROR: " + e.getMessage());
            }
        }
    }

    private class IncomingReader implements Runnable {
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    final String message = line;
                    SwingUtilities.invokeLater(() -> {
                        if (message.startsWith("CLIENT")) {
                            String[] parts = message.split(": ", 2);
                            String sender = parts[0];
                            String cipherText = parts[1];
                            String decrypted = RSAUtil.decrypt(cipherText, myD, myN);
                            chatArea.append(sender + ": " + decrypted + "\n");
                            logSecurity("CRYPTO_IN: " + cipherText);
                        } else {
                            chatArea.append("SYSTEM: " + message + "\n");
                        }
                        // Auto-scroll
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> chatArea.append("SYSTEM: Disconnected from server.\n"));
            }
        }
    }
}
