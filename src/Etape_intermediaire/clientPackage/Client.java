package Etape_intermediaire.clientPackage;

import Etape_intermediaire.shared.ReceptionRunnable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.*;
public class Client {
    public static void main(String[] args) throws Exception{
        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(1235));
        InetAddress adresseServeur = InetAddress.getByName("localhost");
        int portServeur = 1234;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre nom d'utilisateur : ");
        String nomUtilisateur = scanner.nextLine();

        Thread receptionThread = new Thread(new ReceptionRunnable(socket));
        receptionThread.start();
        while (true){
            System.out.print(nomUtilisateur + " : ");

            // Lire le message saisi par l'utilisateur
            String message = scanner.nextLine();

            // Préfixer le message avec le nom de son expéditeur
            String messageComplet =
                    "[" + nomUtilisateur + "] : " + message;

            // Convertir le message en octets UTF-8
            byte[] bufferEnvoi =
                    messageComplet.getBytes(StandardCharsets.UTF_8);

            // Construire le paquet destiné au serveur
            DatagramPacket packetEnvoi =
                    new DatagramPacket(
                            bufferEnvoi,
                            bufferEnvoi.length,
                            adresseServeur,
                            portServeur
                    );

            // Envoyer le message au serveur.
            // Le serveur se chargera de le diffuser aux autres clients.
            socket.send(packetEnvoi);

        }
    }
}
