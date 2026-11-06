package Etape2.serverPackage;
import java.io.IOException;
import java.net.*;
public class Server {
    static byte[] buffer = new byte[1024];
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(1234));
        while (true){
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
            socket.receive(packet);
            int taille = packet.getLength();
            String donnees = new String(packet.getData(),0,taille);
            System.out.println(donnees);
        }
    }
}
