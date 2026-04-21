package fr.univ.uppa.reservation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {

    @Test
    void test_create_ok() {
        // 1. Create OK
        ReservationService service = new ReservationService();
        Result<Reservation> res = service.createReservation("Alice", 1L, 
                LocalDateTime.parse("2026-02-10T10:00"), 
                LocalDateTime.parse("2026-02-10T11:00"));
        
        assertTrue(res.isOk(), "La réservation doit réussir");
        
        assertEquals(1L, res.value().id(), "L'ID doit être 1");
        assertEquals(Status.CONFIRMED, res.value().status());
    }

    @Test
    void test_create_overlap_conflict() {
        // 2. Overlap -> CONFLICT
        ReservationService service = new ReservationService();
        service.createReservation("Alice", 1L, 
                LocalDateTime.parse("2026-02-10T10:00"), 
                LocalDateTime.parse("2026-02-10T11:00"));

        Result<Reservation> res = service.createReservation("Bob", 1L, 
                LocalDateTime.parse("2026-02-10T10:30"), 
                LocalDateTime.parse("2026-02-10T11:30"));

        assertFalse(res.isOk());
        assertEquals(ErrorCode.CONFLICT, res.error(), "Il doit y avoir un conflit");
    }

    @Test
    void test_create_boundary_not_conflict() {
        // 3. Boundary OK (pas conflit)
        ReservationService service = new ReservationService();
        service.createReservation("Alice", 1L, 
                LocalDateTime.parse("2026-02-10T10:00"), 
                LocalDateTime.parse("2026-02-10T11:00"));

        Result<Reservation> res = service.createReservation("Bob", 1L, 
                LocalDateTime.parse("2026-02-10T11:00"), 
                LocalDateTime.parse("2026-02-10T12:00"));

        assertTrue(res.isOk(), "Pas de conflit sur les bornes exactes");
    }

    @Test
    void test_cancel_frees_slot() {
        // 4. Cancel libère le créneau
        ReservationService service = new ReservationService();
        
        // Alice réserve
        Result<Reservation> resAlice = service.createReservation("Alice", 1L, 
                LocalDateTime.parse("2026-02-10T10:00"), 
                LocalDateTime.parse("2026-02-10T11:00"));
                
        // Alice annule id = 1
        service.cancel(resAlice.value().id());

        // Bob tente de réserver exactement sur le même créneau
        Result<Reservation> resBob = service.createReservation("Bob", 1L, 
                LocalDateTime.parse("2026-02-10T10:00"), 
                LocalDateTime.parse("2026-02-10T11:00"));

        assertTrue(resBob.isOk(), "La réservation de Bob doit passer car celle d'Alice est annulée");
    }
}