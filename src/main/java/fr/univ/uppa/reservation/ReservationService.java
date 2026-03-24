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

    public List<Reservation> allReservations() {
        return List.copyOf(reservations);
    }

    public Result<Reservation> createReservation(long resourceId, LocalDateTime start, LocalDateTime end) {
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
            if (r.resourceId() == resourceId && overlap(r.start(), r.end(), start, end)) {
                return Result.fail(ErrorCode.CONFLICT); 
            }
        }
        // TODO: if ok -> store new reservation and return ok
        Reservation newRes = new Reservation(nextId++, resourceId, start, end);
        reservations.add(newRes);
        return Result.ok(newRes);
     
    }

    static boolean overlap(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        // Pas de chevauchement sur la même ressource
        return s1.isBefore(e2) && s2.isBefore(e1);
    }
}
