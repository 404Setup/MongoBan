package one.tranic.mongoban.api.parse.network;

import com.google.common.io.Resources;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class NetworkParser {

    /**
     * Checks if an InetAddress is a private IP address.
     *
     * @param address the InetAddress to check
     * @return true if the address is a private IP, false otherwise
     */
    public static boolean isPrivateIp(InetAddress address) {
        return (address.isAnyLocalAddress() // Matches 0.0.0.0/8 and ::
                || address.isLoopbackAddress() // Matches 127.0.0.0/8 and ::1
                || address.isLinkLocalAddress() // Matches 169.254.0.0/16 and fe80::/10
                || address.isSiteLocalAddress() // Matches 10.0.0.0/8, 172.16.0.0/12, and 192.168.0.0/16
                || isNatRange(address)); // Matches NAT-internal ranges like Carrier-Grade NAT (e.g., 100.64.0.0/10)
    }

    /**
     * Checks if a given IP address in String format is a private IP address.
     *
     * @param address the IP address as a String to check
     * @return true if the address is a private IP, false otherwise
     * @throws UnknownHostException if the string cannot be parsed as an IP address
     */
    public static boolean isPrivateIp(String address) throws UnknownHostException {
        return isPrivateIp(InetAddress.getByName(address));
    }

    /**
     * Helper method to detect extended NAT internal ranges.
     *
     * @param address the InetAddress to check
     * @return true if part of NAT internal ranges, false otherwise
     */
    public static boolean isNatRange(InetAddress address) {
        byte[] bytes = address.getAddress();
        if (bytes.length == 4) { // IPv4
            int firstOctet = bytes[0] & 0xFF;
            int secondOctet = bytes[1] & 0xFF;
            int prefix = (firstOctet << 8) | secondOctet;

            // Carrier-Grade NAT 100.64.0.0/10
            return prefix >= 0x6440 && prefix <= 0x647F;
        }
        return false;
    }

    /**
     * Retrieves an InputStream for a specified resource file by its filename.
     *
     * @param filename the name of the resource file to be loaded
     * @return an InputStream for the resource file, or null if the resource cannot be found or accessed
     */
    public static InputStream resource(@NotNull String filename) {
        try {
            URL url = Resources.getResource(filename);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
