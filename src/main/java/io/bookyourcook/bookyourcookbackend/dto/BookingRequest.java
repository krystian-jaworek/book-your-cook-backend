package io.bookyourcook.bookyourcookbackend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingRequest {
    private String cookId;
    private LocalDate date;
    private String time; // To be implemented later
    private List<BookedMenuItem> menu;
    private boolean shopping;

    @Data
    public static class BookedMenuItem {
        private String id;
        private String name;
        private int portions;
    }
}
