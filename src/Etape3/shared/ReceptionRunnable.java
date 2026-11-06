package Etape3.shared;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class ReceptionRunnable implements Runnable {
    DatagramSocket socket;
    public ReceptionRunnable(DatagramSocket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        while (true) {

            try {
                // Préparer le tableau de réception
                byte[] bufferReception = new byte[1024];

                // Créer le paquet qui contiendra le message reçu
                DatagramPacket packetReception =
                        new DatagramPacket(
                                bufferReception,
                                bufferReception.length
                        );

                // Attendre un message provenant du serveur
                socket.receive(packetReception);

                // Convertir les octets reçus en texte
                String message = new String(
                        packetReception.getData(),
                        0,
                        packetReception.getLength(),
                        StandardCharsets.UTF_8
                );

                // Afficher le message d'un autre client
                System.out.println(
                        "\n" + message
                );

            } catch (Exception e) {
                // Arrêter le thread si le socket est fermé
                if (socket.isClosed()) {
                    break;
                }

                e.printStackTrace();
            }
        }
    }
}

