package fr.univ.uppa.reservation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {

    @Test
    void test_create_ok() {
        // Given : une ressource existante et des dates valides
        ReservationService service = new ReservationService();
        LocalDateTime start = LocalDateTime.of(2026, 3, 24, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 24, 11, 0);

        // When : on crée la réservation
        Result<Reservation> result = service.createReservation(1L, start, end);

        // Then : La reservation est créée et recoit l'ID 1
        assertTrue(result.isOk(), "La réservation a été acceptée");
        assertEquals(1L, result.value().id());
    }

    @Test
    void test_create_overlap_conflict() {
        // Given : une réservation existe déjà de 10h à 11h
        ReservationService service = new ReservationService();
        service.createReservation(1L, LocalDateTime.of(2026, 3, 24, 10, 0), LocalDateTime.of(2026, 3, 24, 11, 0));

        // When : on tente de réserver de 10h30 à 11h30 (chevauchement)
        Result<Reservation> result = service.createReservation(1L, 
                LocalDateTime.of(2026, 3, 24, 10, 30), 
                LocalDateTime.of(2026, 3, 24, 11, 30));

        // Then : on doit avoir une erreur de type CONFLICT
        assertFalse(result.isOk());
        assertEquals(ErrorCode.CONFLICT, result.error());
    }

    @Test
    void test_create_boundary_not_conflict() {
        // Given : une réservation finit à 11h
        ReservationService service = new ReservationService();
        service.createReservation(1L, LocalDateTime.of(2026, 3, 24, 10, 0), LocalDateTime.of(2026, 3, 24, 11, 0));

        // When : la suivante commence à 11h pile
        Result<Reservation> result = service.createReservation(1L, 
                LocalDateTime.of(2026, 3, 24, 11, 0), 
                LocalDateTime.of(2026, 3, 24, 12, 0));

        // Then : pas de conflit
        assertTrue(result.isOk(), "Le cas limite ne devrait pas être en conflit");
    }
}