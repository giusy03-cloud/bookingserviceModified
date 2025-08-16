package com.dipartimento.bookingservice.domain;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // viene da User & Auth

    @Column(name = "event_id", nullable = false)
    private Long eventId; // viene da Event Service



    public Long getId() {
        return id;
    }

    public void setId(Long _id) {
        id = _id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }


}
