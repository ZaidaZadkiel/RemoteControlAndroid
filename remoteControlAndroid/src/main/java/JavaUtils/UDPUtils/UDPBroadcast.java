package JavaUtils.UDPUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPBroadcast {

    public static void startNewBroadcastRequest(final int port, final String request,
                                                boolean startNewThread, final int timeout,
                                                final UDPBroadcastResponseListener responseListener) {
        if (startNewThread) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    _startNewBroadcastRequest(port, request, timeout, responseListener, "255.255.255.255");
                }

            }).start();
        } else {
            _startNewBroadcastRequest(port, request, timeout, responseListener, "255.255.255.255");
        }
    }

    public static void startNewBroadcastRequest(final int port, final String request,
                                                boolean startNewThread, final int timeout,
                                                final UDPBroadcastResponseListener responseListener, final String defaultBroadcastIP) {
        if (startNewThread) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    _startNewBroadcastRequest(port, request, timeout, responseListener, defaultBroadcastIP);
                }

            }).start();
        } else {
            _startNewBroadcastRequest(port, request, timeout, responseListener, defaultBroadcastIP);
        }
    }

    private static void _startNewBroadcastRequest(int port, String request, int timeout,
                                                  UDPBroadcastResponseListener responseListener, String defaultBroadcastIP) {
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);
            byte[] sendData = request.getBytes();
            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(defaultBroadcastIP), port);
                c.send(sendPacket);
                System.out.println(">>> Request packet sent to: " + defaultBroadcastIP + " (DEFAULT)");
            } catch (Exception e) {
            }
            
            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, port);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }
                    System.out.println(">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println(">>> Done looping over all network interfaces. Now waiting for a reply!");
            //Wait for a response
            long now = System.currentTimeMillis();
            c.setSoTimeout(timeout);
            ArrayList<String> hostNames = new ArrayList<String>();
            while (now + timeout > System.currentTimeMillis()) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                try {
                    c.receive(receivePacket);
                } catch (SocketTimeoutException e) {
                    System.out.println(">>> Reply Timeout!");
                    break;
                }

                now = System.currentTimeMillis();
                //We have a response
                if (!hostNames.contains(receivePacket.getAddress().getHostAddress())) {
                    System.out.println(">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
                    //Check if the message is correct
                    String message = new String(receivePacket.getData()).trim();
                    responseListener.process(message, receivePacket.getAddress());
                    hostNames.add(receivePacket.getAddress().getHostAddress());
                }
            }
            //Close the port!
            c.close();
        } catch (IOException ex) {
            Logger.getLogger(UDPBroadcast.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void startBroadcastReceiver(final int port,
                                              final UDPBroadcastRequestListener requestListener) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                _startBroadcastReceiver(port, requestListener);
            }

        }).start();
    }

    private static void _startBroadcastReceiver(int port,
                                                UDPBroadcastRequestListener requestListener) {
        try {
            // Keep a socket open to listen to all the UDP trafic that is
            // destined for this port
            DatagramSocket socket = new DatagramSocket(port,
                    InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            while (true) {
                // Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf,
                        recvBuf.length);
                socket.receive(packet);
                // Packet received
                // See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                byte[] sendData = requestListener.process(message, packet.getAddress()).getBytes();
                // Send a response
                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, packet.getAddress(),
                        packet.getPort());
                socket.send(sendPacket);
            }
        } catch (IOException ex) {
            Logger.getLogger(UDPBroadcast.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

    }

    public static abstract class UDPBroadcastResponseListener {
        public abstract void process(String response, InetAddress address);
    }

    public static abstract class UDPBroadcastRequestListener {
        public abstract String process(String request, InetAddress address);
    }

}
