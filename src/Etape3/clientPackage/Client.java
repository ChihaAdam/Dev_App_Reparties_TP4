package Etape3.clientPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try {
            // 1. Création du socket UDP du client.
            // Le système attribue automatiquement un port UDP disponible.
            DatagramSocket socket = new DatagramSocket();

            // 2. Adresse IP et port du serveur
            InetAddress adresseServeur =
                    InetAddress.getByName("localhost");

            int portServeur = 1234;

            // 3. Préparer la saisie au clavier
            Scanner scanner = new Scanner(System.in);

            System.out.print("Entrez votre nom d'utilisateur : ");
            String nomUtilisateur = scanner.nextLine();

            // 4. Créer un thread chargé de recevoir les messages diffusés
            Thread threadReception = new Thread(new Runnable() {

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
            });

            // 5. Démarrer le thread de réception.
            // La réception peut attendre sans bloquer la saisie au clavier.
            threadReception.start();

            // 6. Thread principal : saisie et envoi des messages
            while (true) {

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

