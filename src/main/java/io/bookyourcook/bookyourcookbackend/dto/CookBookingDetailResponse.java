package io.bookyourcook.bookyourcookbackend.dto;

import io.bookyourcook.bookyourcookbackend.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CookBookingDetailResponse {
    private String id;
    private LocalDate date;
    private String time;
    private String status;
    private boolean shopping;
    private List<Booking.BookedMenuItem> menu;
    private CustomerInfo customer;
    private double totalCost;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String name;
        private String contact;
        private String address;
    }
}
