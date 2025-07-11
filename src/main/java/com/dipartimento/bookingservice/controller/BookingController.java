package com.dipartimento.bookingservice.controller;

import com.dipartimento.bookingservice.domain.Booking;
import com.dipartimento.bookingservice.repository.BookingRepository;
import com.dipartimento.bookingservice.security.util.JwtUtil;
import com.dipartimento.bookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private JwtUtil jwtUtil;







    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody Booking booking, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userIdFromToken = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractUserRole(token);

            // Controlla che solo i partecipanti possano prenotare
            if (!"PARTICIPANT".equalsIgnoreCase(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo i partecipanti possono effettuare prenotazioni");
            }

            // Usa sempre l'userId estratto dal token
            booking.setUserId(userIdFromToken);

            // Verifica che l'evento esista
            if (!bookingService.isEventExists(booking.getEventId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato");
            }

            bookingService.createBooking(booking.getUserId(), booking.getEventId(), booking.getBookingTime());

            return ResponseEntity.ok("Prenotazione effettuata con successo");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la prenotazione");
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("ID ricevuto per cancellazione: " + id);

            String token = authHeader.replace("Bearer ", "");
            Long userIdFromToken = JwtUtil.extractUserId(token);
            String role = JwtUtil.extractUserRole(token);

            Booking booking = bookingService.getBookingById(id);
            if (booking == null) {
                System.out.println("Booking con id " + id + " non trovato.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prenotazione non trovata");
            }

            System.out.println("Role: '" + role + "'");
            System.out.println("UserId from token: " + userIdFromToken);
            System.out.println("Booking userId: " + booking.getUserId());
            System.out.println("Is owner? " + booking.getUserId().equals(userIdFromToken));
            System.out.println("Is organizer? " + "ORGANIZER".equals(role));

            if (!booking.getUserId().equals(userIdFromToken) && !"ORGANIZER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non sei autorizzato a cancellare questa prenotazione");
            }

            boolean deleted = bookingService.deleteById(id);
            return deleted
                    ? ResponseEntity.ok("Prenotazione cancellata con successo!")
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nella cancellazione");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno del server");
        }
    }



    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String role = JwtUtil.extractUserRole(token);

            if (!"ORGANIZER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Non sei autorizzato a vedere tutte le prenotazioni");
            }

            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore interno del server");
        }
    }


    // GET prenotazioni per userId (Long)

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingByUserId(@PathVariable Long userId,
                                                @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String role = JwtUtil.extractUserRole(token);

        if (!"ORGANIZER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non sei autorizzato a vedere queste prenotazioni");
        }

        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        if (bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Booking>> getBookingByEventId(@PathVariable Long eventId,
                                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String role = JwtUtil.extractUserRole(token);

            if (!"ORGANIZER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
            }

            List<Booking> bookings = bookingService.getBookingByEventId(eventId);
            if (bookings.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
            return ResponseEntity.ok(bookings);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Endpoint usato dal ReviewService per verificare se un utente ha prenotato un evento
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserBookedEvent(@RequestParam Long userId,
                                                      @RequestParam Long eventId) {
        try {
            boolean hasBooked = bookingService.hasUserBookedEvent(userId, eventId);
            return ResponseEntity.ok(hasBooked);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }


    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteBookingsByUserId(@PathVariable Long userId,
                                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("[BookingController] Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[BookingController] Header Authorization mancante o malformato");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Header Authorization mancante o malformato");
        }

        String token = authHeader.replace("Bearer ", "");
        String role = JwtUtil.extractUserRole(token);
        System.out.println("[BookingController] Ruolo utente: " + role);

        if (!"ORGANIZER".equals(role)) {
            System.out.println("[BookingController] Utente non autorizzato");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non sei autorizzato a cancellare queste prenotazioni");
        }

        System.out.println("[BookingController] Eliminazione prenotazioni per userId: " + userId);
        bookingRepository.deleteByUserId(userId);
        return ResponseEntity.ok("Prenotazioni eliminate");
    }





}
