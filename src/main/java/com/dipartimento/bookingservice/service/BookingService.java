package com.dipartimento.bookingservice.service;


import com.dipartimento.bookingservice.domain.Booking;
import com.dipartimento.bookingservice.dto.UsersAccounts;
import com.dipartimento.bookingservice.repository.BookingRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String AUTH_ME_URL = "http://localhost:8080/auth/me";

    private final String EVENT_SERVICE_URL = "http://localhost:8081/events"; // URL Event service

    public boolean isUserAuthenticated(Long userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UsersAccounts> response = restTemplate.exchange(
                    AUTH_ME_URL,
                    HttpMethod.GET,
                    entity,
                    UsersAccounts.class
            );

            UsersAccounts user = response.getBody();
            return user != null && user.getId()==userId && "PARTICIPANT".equalsIgnoreCase(user.getRole());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Metodo per verificare se l'evento esiste chiamando Event service

    public boolean isEventExists(Long eventId) {
        try {
            String url = EVENT_SERVICE_URL + "/public/" + eventId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public Booking createBooking(Long userId, Long eventID, LocalDateTime bookingTime) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setEventId(eventID);
        booking.setBookingTime(bookingTime != null ? bookingTime : LocalDateTime.now());
        return bookingRepository.save(booking);
    }


    //Controllo se una pernotazione esiste o no per eliminarla:
    public boolean deleteById(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            return false;
        }
        bookingRepository.deleteById(bookingId);
        return true;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }




    public List<Booking> getAllBookings(){
        return bookingRepository.findAll();
    }

    //Chiamo il metodo della repository che cerca le prenotazioni fatte da un utente:
    public List<Booking> getBookingsByUserId(Long id){
        return bookingRepository.findByUserId(id);
    }

    //Chiamo il metodo della repository che cerca le prenotazioni per quell'evento:
    public List<Booking> getBookingByEventId(Long id){
        return bookingRepository.findByEventId(id);
    }
    public boolean hasUserBookedEvent(Long userId, Long eventId) {
        List<Booking> bookings = bookingRepository.findByUserIdAndEventId(userId, eventId);
        return !bookings.isEmpty();
    }



}
