package io.bookyourcook.bookyourcookbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicCalendarResponse {
    private CookInfo cook;
    private List<AvailabilityDto> availabilities;
    private Map<LocalDate, List<BookedSlotDto>> bookedSlots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CookInfo {
        private String id;
        private String firstName;
        private String lastName;
        private String profileImageUrl;
        private String bio;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityDto {
        private LocalDate date;
        private String from;
        private String to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookedSlotDto {
        private String time;
    }
}
