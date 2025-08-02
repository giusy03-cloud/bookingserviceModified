package com.dipartimento.bookingservice.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean processMockPayment(Long userId, Long eventId, double amount) {
        // Simulazione logica di pagamento: sempre successo
        System.out.println("Processando pagamento fittizio per utente " + userId + " per evento " + eventId + " con importo €" + amount);
        return true;
    }
}
