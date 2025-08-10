package io.bookyourcook.bookyourcookbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCalendarResponse {

    private List<BookingDetails> bookings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingDetails {
        private String id;
        private LocalDate date;
        private String time;
        private String status;
        private CookInfo cook;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CookInfo {
        private String firstName;
        private String lastName;
        private String profileImageUrl;
    }
}
