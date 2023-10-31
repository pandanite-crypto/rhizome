package rhizome.core.common;

import org.json.JSONObject;

import rhizome.core.transaction.TransactionAmount;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.Duration;

public class Helpers {

    private Helpers() {}

    public static long hostToNetworkLong(long x) {
        return Long.reverseBytes(x);
    }

    public static int hostToNetworkInt(int x) {
        return Integer.reverseBytes(x);
    }

    public static long networkToHostLong(long x) {
        return Long.reverseBytes(x);
    }

    public static int networkToHostInt(int x) {
        return Integer.reverseBytes(x);
    }

    public static TransactionAmount PDN(double amount) {
        return new TransactionAmount((long) (amount * Constants.DECIMAL_SCALE_FACTOR));
    }

    // Assume ByteBuffer methods are defined elsewhere or replace with appropriate code
    public static int readNetworkInt(ByteBuffer buffer) {
        return networkToHostInt(buffer.getInt());
    }

    public static long readNetworkLong(ByteBuffer buffer) {
        return networkToHostLong(buffer.getLong());
    }

    // TODO: Other readNetwork and writeNetwork methods ...

    public static String randomString(int len) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static void writeJsonToFile(JSONObject data, String filepath) throws IOException {
        String dataStr = data.toString();
        try (FileWriter output = new FileWriter(filepath)) {
            output.write(dataStr);
        }
    }

    public static JSONObject readJsonFromFile(String filepath) throws IOException {
        StringBuilder jsonStr;
        try (BufferedReader input = new BufferedReader(new FileReader(filepath))) {
            String line;
            jsonStr = new StringBuilder();
            while ((line = input.readLine()) != null) {
                jsonStr.append(line);
            }
        }
        return new JSONObject(jsonStr.toString());
    }

    public static long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    public static String longToString(long t) {
        return Long.toString(t);
    }

    public static long stringToLong(String input) {
        return Long.parseLong(input);
    }

    public static String exec(String cmd) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        return output.toString();
    }

    public static long getTimeMilliseconds() {
        return Duration.between(Instant.EPOCH, Instant.now()).toMillis();
    }
}
