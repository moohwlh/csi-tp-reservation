package fr.univ.uppa.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ReservationService service = new ReservationService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Système prêt. Commandes : resources, reserve, reservations, cancel, quit");

        while (true) {
            // Lire une ligne utilisateur 
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            // split
            String[] parts = line.split(" ");
            String command = parts[0];

            // Traiter la commande
            if (command.equals("quit")) {
                System.out.println("Au revoir !");
                break;

            } else if (command.equals("resources")) {
                System.out.println("OK ressources: 1, 2");

            } else if (command.equals("reservations")) {
                List<Reservation> all = service.listReservations();
                System.out.println("OK " + all.size() + " reservations trouvées");
                for (Reservation r : all) {
                    System.out.println(r); // Affiche le record directement
                }

            } else if (command.equals("reserve")) {
                // reserve Alice 1 2026-02-10T10:00 2026-02-10T11:00
                String user = parts[1];
                long resourceId = Long.parseLong(parts[2]);
                LocalDateTime start = LocalDateTime.parse(parts[3]);
                LocalDateTime end = LocalDateTime.parse(parts[4]);

                Result<Reservation> result = service.createReservation(user, resourceId, start, end);

                if (result.isOk()) {
                    System.out.println("OK reservationId=" + result.value().id());
                } else {
                    System.out.println("ERR " + result.error() + " echec de la reservation");
                }

            } else if (command.equals("cancel")) {
                // cancel 1
                long id = Long.parseLong(parts[1]);
                Result<Reservation> result = service.cancel(id);

                if (result.isOk()) {
                    System.out.println("OK annulation de reservationId=" + result.value().id());
                } else {
                    System.out.println("ERR " + result.error() + " introuvable");
                }

            } else {
                System.out.println("ERR COMMANDE_INCONNUE");
            }
        }
        
        scanner.close();
    }
}