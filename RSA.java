import java.util.Scanner;
import java.math.BigInteger;

public class RSA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String text;
        int choi;
        BigInteger keypu, keypr, n;

        while (true) {
            System.out.println("\n\n1. Encryption.");
            System.out.println("2. Decryption.");
            System.out.println("3. Cryptanalysis.");
            System.out.print("Choose Your Option: ");
            choi = scanner.nextInt();
            scanner.nextLine();  
            
            switch (choi) {
                case 1:
                    System.out.print("Enter Your Plain Text: ");
                    text = scanner.nextLine();
                    System.out.print("Enter Your Public Key (e): ");
                    keypu = scanner.nextBigInteger();
                    System.out.print("Enter Your Modulus (n): ");
                    n = scanner.nextBigInteger();
                    System.out.print("Your Cipher Text is: ");
                    for (int i = 0; i < text.length(); i++) {
                        int charValue = (int) text.charAt(i);  // ASCII conversion
                        BigInteger ch = BigInteger.valueOf(charValue);
                        BigInteger encryptedCh = ch.modPow(keypu, n);
                        System.out.print(encryptedCh + " ");
                    }
                    System.out.println(); 
                    break;

                case 2:
                    System.out.print("Enter Your Cipher Text (space-separated integers): ");
                    String[] cipherText = scanner.nextLine().split(" ");
                    System.out.print("Enter Your Private Key (d): ");
                    keypr = scanner.nextBigInteger();
                    System.out.print("Enter Your Modulus (n): ");
                    n = scanner.nextBigInteger();
                    System.out.print("Your Plain Text is: ");
                    for (String cipher : cipherText) {
                        BigInteger encryptedCh = new BigInteger(cipher);
                        BigInteger decryptedCh = encryptedCh.modPow(keypr, n);
                        int charValue = decryptedCh.intValue();  
                        System.out.print((char) charValue);  // ASCII conversion
                    }
                    System.out.println();  
                    break;
                default:
                    System.out.println("Invalid Option. Please choose 1 or 2.");
                    break;
            }
        }
    }
}