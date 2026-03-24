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
        // TODO: Rule 2 - validate resource exists (1 or 2)
        // TODO: Rule 3 - check overlap on same resource
        // TODO: if ok -> store new reservation and return ok
        return Result.fail(ErrorCode.CONFLICT); // placeholder
    }

    static boolean overlap(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        // TODO: implement using: s1 < e2 AND s2 < e1
        return false;
    }
}
