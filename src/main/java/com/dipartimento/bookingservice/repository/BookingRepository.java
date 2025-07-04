package com.dipartimento.bookingservice.repository;


import com.dipartimento.bookingservice.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

//L'interfaccia repository è utile per gestire l'accesso al database in modo automatico.
// Infatti in questo modo si evita di scrivere le query di ogni metodo perche
// sto gia comprendendo i metodi save, findById(id), findAll(), deleteById(id)
// e posso anche aggiungerne altri

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //Metodo personalizzato che permette di cercare le prenotazioni di un utente dato:
    List<Booking> findByUserId(Long userId);

    //Metodo peronalizzato che permette di cercare le prenotazioni di un evento dato:
    List<Booking> findByEventId(Long eventId);
}
