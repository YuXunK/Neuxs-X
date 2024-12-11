package org.yuxun.x.nexusx.Utils;

import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Component
public class MagicPacketUtils {

    public static void sendMagicPacket(String macAddress, String broadcastIP) throws Exception {
        byte[] macBytes = getMacBytes(macAddress);
        byte[] magicPacket = new byte[102];

        // Magic Packet: 6 bytes of 0xFF
        for (int i = 0; i < 6; i++) {
            magicPacket[i] = (byte) 0xFF;
        }

        // Append MAC address 16 times
        for (int i = 6; i < magicPacket.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, magicPacket, i, macBytes.length);
        }

        // Create socket and send packet
        InetAddress address = InetAddress.getByName(broadcastIP);
        DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, 9); // UDP port 9
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
        }
    }

    private static byte[] getMacBytes(String macAddress) throws IllegalArgumentException {
        String[] macParts = macAddress.split("[:-]");
        if (macParts.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address format.");
        }

        byte[] macBytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            macBytes[i] = (byte) Integer.parseInt(macParts[i], 16);
        }
        return macBytes;
    }
}
