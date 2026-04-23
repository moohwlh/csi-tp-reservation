package fr.univ.uppa.reservation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

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
    @Test
    void test_save_load_conserve_reservations() {
        ReservationService service1 = new ReservationService();
        ReservationCsvStore store = new ReservationCsvStore();
        String testFile = "test_reservations.csv";

        // On crée une réservation
        service1.createReservation("Alice", 1L, 
                LocalDateTime.parse("2026-03-20T10:00"), 
                LocalDateTime.parse("2026-03-20T11:00"));

        // On sauvegarde
        store.save(testFile, service1.listReservations());

        // On recharge dans un NOUVEAU service
        ReservationService service2 = new ReservationService();
        List<Reservation> loaded = store.load(testFile);
        service2.replaceReservations(loaded);

        // Vérification
        assertEquals(1, service2.listReservations().size(), "Il doit y avoir 1 réservation chargée");
        assertEquals("Alice", service2.listReservations().get(0).user(), "L'utilisateur doit être Alice");
    }

    @Test
    void test_cancelled_remains_cancelled_after_load() {
        ReservationService service1 = new ReservationService();
        ReservationCsvStore store = new ReservationCsvStore();
        String testFile = "test_reservations.csv";

        // Alice réserve puis annule
        Result<Reservation> res = service1.createReservation("Alice", 1L, 
                LocalDateTime.parse("2026-03-20T10:00"), 
                LocalDateTime.parse("2026-03-20T11:00"));
        service1.cancel(res.value().id());

        // On sauvegarde et recharge
        store.save(testFile, service1.listReservations());
        ReservationService service2 = new ReservationService();
        service2.replaceReservations(store.load(testFile));

        // Vérification
        assertEquals(Status.CANCELLED, service2.listReservations().get(0).status(), "Le statut CANCELLED doit être conservé");
    }

    @Test
    void test_findByUser_returns_correct_result() {
        ReservationService service = new ReservationService();
        
        service.createReservation("Alice", 1L, 
                LocalDateTime.parse("2026-03-20T10:00"), 
                LocalDateTime.parse("2026-03-20T11:00"));
                
        service.createReservation("Bob", 2L, 
                LocalDateTime.parse("2026-03-20T14:00"), 
                LocalDateTime.parse("2026-03-20T15:00"));

        List<Reservation> aliceRes = service.findByUser("Alice");
        
        assertEquals(1, aliceRes.size(), "Alice ne doit avoir qu'une seule réservation");
        assertEquals("Alice", aliceRes.get(0).user());
    }
}