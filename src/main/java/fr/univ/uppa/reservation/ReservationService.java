package fr.univ.uppa.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ReservationService {

    private static boolean resourceExists(long resourceId) {
        return resourceId == 1L || resourceId == 2L;
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private long nextId = 1;

    public List<Reservation> listReservations() {
        return List.copyOf(reservations);
    }

    public Result<Reservation> createReservation(String user,long resourceId, LocalDateTime start, LocalDateTime end) {
        // TODO: Rule 1 - validate time: start < end
        if (!start.isBefore(end)) {
            return Result.fail(ErrorCode.INVALID_TIME); 
        }
        // TODO: Rule 2 - validate resource exists (1 or 2)
        if (!resourceExists(resourceId)) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND); 
        }
        // TODO: Rule 3 - check overlap on same resource
        for (Reservation r : reservations) {
            // NOUVEAU : On ignore les réservations annulées pour la détection de conflit
            if (r.status() != Status.CANCELLED && r.resourceId() == resourceId && overlap(r.start(), r.end(), start, end)) {
                return Result.fail(ErrorCode.CONFLICT);
            }
        }
        // TODO: if ok -> store new reservation and return ok
        Reservation newRes = new Reservation(nextId++, user, resourceId, start, end, Status.CONFIRMED);
        reservations.add(newRes);
        return Result.ok(newRes);
     
    }

    public Result<Reservation> cancel(long reservationId) {
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            if (r.id() == reservationId) {
                // On crée une copie avec le statut CANCELLED et on remplace l'ancienne
                Reservation cancelledRes = new Reservation(r.id(), r.user(), r.resourceId(), r.start(), r.end(), Status.CANCELLED);
                reservations.set(i, cancelledRes);
                return Result.ok(cancelledRes);
            }
        }
        // Si on  trouve pas l'ID
        return Result.fail(ErrorCode.NOT_FOUND);
    }

    static boolean overlap(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        // Pas de chevauchement sur la même ressource
        return s1.isBefore(e2) && s2.isBefore(e1);
    }
}
