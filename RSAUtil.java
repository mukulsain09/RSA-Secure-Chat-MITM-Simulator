import java.math.BigInteger;
import java.util.StringJoiner;

public class RSAUtil {
    public static String encrypt(String message, BigInteger e, BigInteger n) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            BigInteger val = BigInteger.valueOf((int) c);
            encrypted.append(val.modPow(e, n).toString()).append(" ");
        }
        return encrypted.toString().trim();
    }

    public static String decrypt(String encryptedMessage, BigInteger d, BigInteger n) {
        try {
            StringBuilder decrypted = new StringBuilder();
            String[] parts = encryptedMessage.split(" ");
            for (String part : parts) {
                if (part.isEmpty()) continue;
                BigInteger val = new BigInteger(part);
                decrypted.append((char) val.modPow(d, n).intValue());
            }
            return decrypted.toString();
        } catch (Exception e) {
            return "[Decryption Error]";
        }
    }
}
