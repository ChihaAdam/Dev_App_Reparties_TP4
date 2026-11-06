package Etape2.clientPackage;
import java.util.*;
import java.net.*;
public class Client {
    public static void main(String[] args) throws Exception{
        DatagramSocket socket = new DatagramSocket();
        Scanner sc = new Scanner(System.in);
        String message;
        int length;
        while (true){
            System.out.println("donner un message");
            message = sc.nextLine();
            length = message.length();
            byte[] buffer = message.getBytes();
            DatagramPacket packet  = new DatagramPacket(buffer,length,InetAddress.getByName("localhost"),1234);
            socket.send(packet);
        }
    }
}
