package fr.univ.uppa.reservation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ReservationCsvStore {

    public List<Reservation> load(String pathStr) {
        List<Reservation> list = new ArrayList<>();
        try {
            Path path = Path.of(pathStr);
            // Si le fichier n'existe pas on renvoie une liste vide
            if (!Files.exists(path)) {
                return list;
            }
            
            List<String> lines = Files.readAllLines(path);
            
            // boucle i = 1 pour sauter la ligne 0 
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue; 
                
                // Découpage des colonnes
                String[] parts = line.split(";");
                long id = Long.parseLong(parts[0]);
                String user = parts[1];
                long resourceId = Long.parseLong(parts[2]);
                LocalDateTime start = LocalDateTime.parse(parts[3]);
                LocalDateTime end = LocalDateTime.parse(parts[4]);
                Status status = Status.valueOf(parts[5]); // Transforme le texte en Status
                
                list.add(new Reservation(id, user, resourceId, start, end, status));
            }
        } catch (Exception e) {
            System.out.println("ERR Lecture CSV : " + e.getMessage());
        }
        return list;
    }

    public void save(String pathStr, List<Reservation> reservations) {
        try {
            List<String> lines = new ArrayList<>();
            // Ajouter l'en-tête obligatoire
            lines.add("id;user;resourceId;start;end;status");
            
            // Ajouter chaque réservation au bon format
            for (Reservation r : reservations) {
                String line = r.id() + ";" + r.user() + ";" + r.resourceId() + ";" 
                            + r.start() + ";" + r.end() + ";" + r.status();
                lines.add(line);
            }
            
            // Écrire le tout dans le fichier
            Files.write(Path.of(pathStr), lines);
        } catch (Exception e) {
            System.out.println("ERR Ecriture CSV : " + e.getMessage());
        }
    }
}