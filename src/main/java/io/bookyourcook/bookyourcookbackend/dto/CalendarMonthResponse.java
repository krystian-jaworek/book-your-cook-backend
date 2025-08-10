package io.bookyourcook.bookyourcookbackend.dto;

import io.bookyourcook.bookyourcookbackend.model.Availability;
import io.bookyourcook.bookyourcookbackend.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO for sending a full month's calendar data to the client.
 */
@Data
@AllArgsConstructor
public class CalendarMonthResponse {
    private List<Availability> availabilities;
    private List<Booking> bookings;
}