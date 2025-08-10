package io.bookyourcook.bookyourcookbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private String cookId;
    private String customerId;
    private String customerName;
    private String customerContact;
    private String address;
    private LocalDate date;
    private String time;
    private List<BookedMenuItem> menu; // Changed from List<MenuItem>
    private boolean shopping;
    private BookingStatus status; // e.g., "UNCONFIRMED", "CONFIRMED", "CANCELED"

    /**
     * Inner class to store simplified information about a menu item
     * within a booking, to avoid duplicating all meal data.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookedMenuItem {
        private String id;
        private String name;
        private int portions;
    }
}
