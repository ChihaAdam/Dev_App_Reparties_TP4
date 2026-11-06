# TP 4 — Sockets UDP : application de chat client/serveur

**Matière :** Développement d’applications réparties  
**Niveau :** LSI 3  
**Année universitaire :** 2026–2027

## 1. Objectif du TP

Ce travail pratique met en œuvre une communication en mode non connecté avec le protocole UDP et les classes `DatagramSocket` et `DatagramPacket` de Java. Le projet progresse d’un échange de messages unidirectionnel vers un chat où le serveur relaie les messages entre plusieurs clients.

## 2. Organisation du projet

Le code est réparti en trois versions, correspondant aux étapes de développement :

| Répertoire | Rôle |
|---|---|
| `src/Etape2/` | Premier échange UDP : le client envoie un datagramme et le serveur le reçoit. |
| `src/Etape_intermediaire/` | Expérimentation de la saisie du nom et de la réception dans un thread dédié. |
| `src/Etape3/` | Version finale du chat : plusieurs clients, réception asynchrone et diffusion par le serveur. |

La version `Etape3` est la version à lancer pour tester le chat complet. Le projet utilise Java (JDK 8 ou plus récent) et peut être ouvert dans IntelliJ IDEA.

## 3. Étape 1 — Préparation

Le projet est organisé en packages Java séparant le code du client, celui du serveur et, pour l’étape intermédiaire, le code de réception partagé. Cette séparation rend les rôles de chaque composant plus faciles à repérer.

## 4. Étape 2 — Premier échange UDP

Dans `Etape2`, le serveur crée un socket associé au port `1234`, puis attend les datagrammes. La taille réelle du message reçu est utilisée lors de sa conversion en texte :

```java
DatagramSocket socket = new DatagramSocket(new InetSocketAddress(1234));
while (true) {
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    socket.receive(packet);
    int taille = packet.getLength();
    String donnees = new String(packet.getData(), 0, taille);
    System.out.println(donnees);
}
```

Le client lit une ligne au clavier, la convertit en tableau d’octets, puis construit un paquet adressé au serveur sur `localhost:1234` :

```java
String message = sc.nextLine();
byte[] buffer = message.getBytes();
DatagramPacket packet = new DatagramPacket(
        buffer, message.length(), InetAddress.getByName("localhost"), 1234);
socket.send(packet);
```

Cette première version vérifie l’envoi et la réception d’un message. Elle ne gère pas encore la diffusion à plusieurs clients ni l’identification de l’expéditeur dans le texte ; ces fonctionnalités sont développées dans les étapes suivantes.

## 5. Étape intermédiaire — Nom et réception asynchrone

L’étape intermédiaire introduit la saisie d’un nom d’utilisateur et un thread de réception. Le texte envoyé est préfixé par le nom :

```java
String messageComplet = "[" + nomUtilisateur + "] : " + message;
byte[] bufferEnvoi = messageComplet.getBytes(StandardCharsets.UTF_8);
```

La classe `ReceptionRunnable` attend les datagrammes dans une boucle distincte de la saisie clavier. Le client peut ainsi continuer à saisir des messages pendant que le thread attend les messages reçus :

```java
Thread receptionThread = new Thread(new ReceptionRunnable(socket));
receptionThread.start();
```

Cette étape prépare la réception asynchrone utilisée dans le chat final.

## 6. Étape 3 — Chat bidirectionnel et diffusion

### 6.1 Serveur UDP

Le serveur final se lie au port `1234` et conserve les adresses des clients qui lui ont envoyé un message. Un client est donc reconnu par son adresse IP et son port UDP :

```java
InetSocketAddress adresseServeur =
        new InetSocketAddress("0.0.0.0", 1234);
socket.bind(adresseServeur);

Set<SocketAddress> clients = new HashSet<SocketAddress>();
```

À chaque réception, le serveur récupère l’adresse de l’expéditeur et l’ajoute à l’ensemble. Un `HashSet` évite d’enregistrer plusieurs fois la même adresse :

```java
socket.receive(packetReception);
SocketAddress adresseExpediteur = packetReception.getSocketAddress();
clients.add(adresseExpediteur);
```

Le serveur envoie ensuite le message à tous les clients connus, sauf à son expéditeur :

```java
byte[] bufferEnvoi = message.getBytes(StandardCharsets.UTF_8);
for (SocketAddress adresseClient : clients) {
    if (!adresseClient.equals(adresseExpediteur)) {
        DatagramPacket packetEnvoi = new DatagramPacket(
                bufferEnvoi, bufferEnvoi.length, adresseClient);
        socket.send(packetEnvoi);
    }
}
```

Le serveur affiche aussi l’adresse de l’expéditeur et le contenu du message dans sa console. La liste des clients est apprise à partir des datagrammes reçus : le client doit donc envoyer un premier message pour être enregistré et pouvoir recevoir les diffusions.

### 6.2 Client UDP

Le client demande un nom d’utilisateur, crée un socket UDP et indique l’adresse du serveur. Le système attribue automatiquement un port local disponible :

```java
DatagramSocket socket = new DatagramSocket();
InetAddress adresseServeur = InetAddress.getByName("localhost");
int portServeur = 1234;
```

Un thread dédié attend les messages relayés par le serveur et les affiche. La conversion utilise UTF-8 ainsi que la longueur effective du datagramme :

```java
socket.receive(packetReception);
String message = new String(
        packetReception.getData(),
        0,
        packetReception.getLength(),
        StandardCharsets.UTF_8);
System.out.println("\n" + message);
```

Dans le thread principal, le client préfixe chaque message avec le nom choisi, le convertit en UTF-8 et l’envoie au serveur :

```java
String messageComplet = "[" + nomUtilisateur + "] : " + message;
byte[] bufferEnvoi = messageComplet.getBytes(StandardCharsets.UTF_8);
DatagramPacket packetEnvoi = new DatagramPacket(
        bufferEnvoi, bufferEnvoi.length, adresseServeur, portServeur);
socket.send(packetEnvoi);
```

Les deux activités sont ainsi concurrentes : le programme principal gère la saisie et l’envoi, tandis que le thread de réception attend les messages entrants.

## 7. Bilan

Ce TP m’a permis de mettre en pratique l’envoi et la réception de datagrammes UDP en Java. La progression du projet montre le passage d’un échange simple à un chat asynchrone : le serveur mémorise les adresses des clients connus puis relaie leurs messages aux autres participants. Contrairement à TCP, UDP ne crée pas de connexion préalable ; l’adresse de destination est portée par chaque datagramme.

