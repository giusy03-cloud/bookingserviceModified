package com.dipartimento.bookingservice.dto;




import java.time.LocalDateTime;

public class BookingWithEventDTO {
    private Long bookingId;
    private Long userId;
    private Long eventId;
    private LocalDateTime bookingTime;

    // Event details
    private EventDTO event;

    // getters e setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public EventDTO getEvent() { return event; }
    public void setEvent(EventDTO event) { this.event = event; }
}

