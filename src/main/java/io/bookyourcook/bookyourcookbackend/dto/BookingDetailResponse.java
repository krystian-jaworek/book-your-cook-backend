package io.bookyourcook.bookyourcookbackend.dto;

import io.bookyourcook.bookyourcookbackend.model.Booking;
import io.bookyourcook.bookyourcookbackend.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
    private String id;
    private LocalDate date;
    private String time;
    private String status;
    private boolean shopping;
    private List<Booking.BookedMenuItem> menu;
    private PublicCalendarResponse.CookInfo cook;
    private double totalCost;
}
