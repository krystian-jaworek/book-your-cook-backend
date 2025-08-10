package io.bookyourcook.bookyourcookbackend.service;

import io.bookyourcook.bookyourcookbackend.dto.CalendarMonthResponse;
import io.bookyourcook.bookyourcookbackend.dto.PublicCalendarResponse;
import io.bookyourcook.bookyourcookbackend.exception.ResourceNotFoundException;
import io.bookyourcook.bookyourcookbackend.model.Availability;
import io.bookyourcook.bookyourcookbackend.model.Booking;
import io.bookyourcook.bookyourcookbackend.model.BookingStatus;
import io.bookyourcook.bookyourcookbackend.model.User;
import io.bookyourcook.bookyourcookbackend.repository.AvailabilityRepository;
import io.bookyourcook.bookyourcookbackend.repository.BookingRepository;
import io.bookyourcook.bookyourcookbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    public CalendarMonthResponse getCalendarDataForMonth(String username, int year, int month) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Availability> availabilities = availabilityRepository.findByCookIdAndDateBetween(user.getId(), startDate, endDate);
        List<Booking> bookings = bookingRepository.findByCookIdAndDateBetween(user.getId(), startDate, endDate);

        return new CalendarMonthResponse(availabilities, bookings);
    }

    public void setAvailability(String username, LocalDate date, String from, String to) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Availability availability = availabilityRepository.findByCookIdAndDate(user.getId(), date)
                .orElse(new Availability());

        availability.setCookId(user.getId());
        availability.setDate(date);
        availability.setFrom(from);
        availability.setTo(to);

        availabilityRepository.save(availability);
    }

    public void removeAvailability(String username, LocalDate date) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        availabilityRepository.findByCookIdAndDate(user.getId(), date)
                .ifPresent(availabilityRepository::delete);
    }

    public PublicCalendarResponse getPublicCalendarForCook(String cookId, int year, int month) {
        User cook = userRepository.findById(cookId)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found with id: " + cookId));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Availability> availabilities = availabilityRepository.findByCookIdAndDateBetween(cook.getId(), startDate, endDate);
        List<PublicCalendarResponse.AvailabilityDto> availabilityDtos = availabilities.stream()
                .map(a -> new PublicCalendarResponse.AvailabilityDto(a.getDate(), a.getFrom(), a.getTo()))
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findByCookIdAndDateBetween(cook.getId(), startDate, endDate);
        Map<LocalDate, List<PublicCalendarResponse.BookedSlotDto>> bookedSlotsMap = bookings.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELED && b.getStatus() != BookingStatus.REJECTED)
                .collect(Collectors.groupingBy(
                        Booking::getDate,
                        Collectors.mapping(b -> new PublicCalendarResponse.BookedSlotDto(b.getTime()), Collectors.toList())
                ));

        PublicCalendarResponse.CookInfo cookInfo = new PublicCalendarResponse.CookInfo(
                cook.getId(),
                cook.getFirstName(),
                cook.getLastName(),
                cook.getProfileImageUrl(),
                cook.getBio()
        );

        return new PublicCalendarResponse(cookInfo, availabilityDtos, bookedSlotsMap);
    }
}
