package Etape3.serverPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class Server{

    public static void main(String[] args) {

        try {
            // 1. Création du socket UDP du serveur
            DatagramSocket socket = new DatagramSocket(null);

            // 2. Association du serveur au port 1234
            InetSocketAddress adresseServeur =
                    new InetSocketAddress("0.0.0.0", 1234);

            socket.bind(adresseServeur);

            System.out.println("Serveur UDP démarré sur le port 1234...");
            System.out.println("En attente des clients...");

            // 3. Ensemble contenant les adresses des clients connus.
            // Un SocketAddress identifie un client par son IP et son port UDP.
            Set<SocketAddress> clients = new HashSet<SocketAddress>();

            // 4. Boucle principale : réception et diffusion des messages
            while (true) {

                // Préparer un tableau pour recevoir les octets
                byte[] bufferReception = new byte[1024];

                // Créer le paquet de réception
                DatagramPacket packetReception =
                        new DatagramPacket(
                                bufferReception,
                                bufferReception.length
                        );

                // Attendre le prochain message d'un client
                socket.receive(packetReception);

                // Récupérer l'adresse IP et le port de l'expéditeur
                SocketAddress adresseExpediteur =
                        packetReception.getSocketAddress();

                // Enregistrer le client s'il n'est pas encore connu
                clients.add(adresseExpediteur);

                // Convertir les octets reçus en chaîne de caractères
                String message = new String(
                        packetReception.getData(),
                        0,
                        packetReception.getLength(),
                        StandardCharsets.UTF_8
                );

                System.out.println(
                        "Message reçu de " + adresseExpediteur
                );
                System.out.println(message);

                // 5. Diffuser le message à tous les autres clients
                for (SocketAddress adresseClient : clients) {

                    // Ne pas renvoyer le message à son expéditeur
                    if (!adresseClient.equals(adresseExpediteur)) {

                        // Convertir le message en tableau d'octets
                        byte[] bufferEnvoi =
                                message.getBytes(StandardCharsets.UTF_8);

                        // Construire un datagramme destiné à ce client
                        DatagramPacket packetEnvoi =
                                new DatagramPacket(
                                        bufferEnvoi,
                                        bufferEnvoi.length,
                                        adresseClient
                                );

                        // Envoyer le message au client destinataire
                        socket.send(packetEnvoi);
                    }
                }

                // Afficher le nombre de clients connus
                System.out.println(
                        "Nombre de clients connus : " + clients.size()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
