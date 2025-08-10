package io.bookyourcook.bookyourcookbackend.service;

import io.bookyourcook.bookyourcookbackend.dto.BookingDetailResponse;
import io.bookyourcook.bookyourcookbackend.dto.BookingRequest;
import io.bookyourcook.bookyourcookbackend.dto.CookBookingDetailResponse;
import io.bookyourcook.bookyourcookbackend.dto.CustomerCalendarResponse;
import io.bookyourcook.bookyourcookbackend.dto.PublicCalendarResponse;
import io.bookyourcook.bookyourcookbackend.exception.ResourceNotFoundException;
import io.bookyourcook.bookyourcookbackend.model.Booking;
import io.bookyourcook.bookyourcookbackend.model.BookingStatus;
import io.bookyourcook.bookyourcookbackend.model.MenuItem;
import io.bookyourcook.bookyourcookbackend.model.User;
import io.bookyourcook.bookyourcookbackend.repository.BookingRepository;
import io.bookyourcook.bookyourcookbackend.repository.MenuItemRepository;
import io.bookyourcook.bookyourcookbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    public Booking createBooking(String customerUsername, BookingRequest request) {
        User customer = userRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerUsername));

        User cook = userRepository.findById(request.getCookId())
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found: " + request.getCookId()));

        Booking booking = new Booking();
        booking.setCookId(cook.getId());
        booking.setCustomerId(customer.getId());
        booking.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        booking.setCustomerContact(customer.getPhoneNumber());
        booking.setAddress(customer.getStreet() + " " + customer.getBuildingNumber() + (customer.getApartmentNumber() != null ? "/" + customer.getApartmentNumber() : "") + ", " + customer.getCity());
        booking.setDate(request.getDate());
        booking.setTime(request.getTime());
        booking.setShopping(request.isShopping());
        booking.setStatus(BookingStatus.UNCONFIRMED);

        List<Booking.BookedMenuItem> bookedMenuItems = request.getMenu().stream()
                .map(item -> new Booking.BookedMenuItem(item.getId(), item.getName(), item.getPortions()))
                .collect(Collectors.toList());
        booking.setMenu(bookedMenuItems);

        return bookingRepository.save(booking);
    }

    public CustomerCalendarResponse getCustomerCalendar(String customerUsername, int year, int month) {
        User customer = userRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerUsername));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        // CORRECTED: The end date is now exclusive (less than the start of the next day)
        LocalDate endDate = yearMonth.atEndOfMonth().plusDays(1);

        // CORRECTED: Call the new repository method
        List<Booking> bookings = bookingRepository.findByCustomerIdAndDateBetween(customer.getId(), startDate, endDate);

        List<String> cookIds = bookings.stream().map(Booking::getCookId).distinct().collect(Collectors.toList());
        Map<String, User> cooksMap = userRepository.findAllById(cookIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<CustomerCalendarResponse.BookingDetails> bookingDetails = bookings.stream().map(booking -> {
            User cook = cooksMap.get(booking.getCookId());
            if (cook == null) return null;

            CustomerCalendarResponse.CookInfo cookInfo = new CustomerCalendarResponse.CookInfo(
                    cook.getFirstName(),
                    cook.getLastName(),
                    cook.getProfileImageUrl()
            );
            return new CustomerCalendarResponse.BookingDetails(
                    booking.getId(),
                    booking.getDate(),
                    booking.getTime(),
                    booking.getStatus().name(),
                    cookInfo
            );
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());

        return new CustomerCalendarResponse(bookingDetails);
    }

    public BookingDetailResponse getBookingDetails(String bookingId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getCustomerId().equals(user.getId())) {
            throw new AccessDeniedException("User is not authorized to view this booking");
        }

        User cook = userRepository.findById(booking.getCookId())
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found for this booking"));

        PublicCalendarResponse.CookInfo cookInfo = new PublicCalendarResponse.CookInfo(
                cook.getId(), cook.getFirstName(), cook.getLastName(), cook.getProfileImageUrl(), cook.getBio()
        );

        List<String> menuItemIds = booking.getMenu().stream().map(Booking.BookedMenuItem::getId).collect(Collectors.toList());
        Map<String, MenuItem> menuItemsMap = menuItemRepository.findAllById(menuItemIds).stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        double ingredientsCost = booking.getMenu().stream()
                .mapToDouble(item -> menuItemsMap.getOrDefault(item.getId(), new MenuItem()).getCost())
                .sum();
        double totalCost = cook.getSessionPrice() + ingredientsCost + (booking.isShopping() ? cook.getShoppingCost() : 0);

        return new BookingDetailResponse(
                booking.getId(),
                booking.getDate(),
                booking.getTime(),
                booking.getStatus().name(),
                booking.isShopping(),
                booking.getMenu(),
                cookInfo,
                totalCost
        );
    }

    public CookBookingDetailResponse getCookBookingDetails(String bookingId, String cookUsername) {
        User cook = userRepository.findByUsername(cookUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found: " + cookUsername));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getCookId().equals(cook.getId())) {
            throw new AccessDeniedException("Cook is not authorized to view this booking");
        }

        CookBookingDetailResponse.CustomerInfo customerInfo = new CookBookingDetailResponse.CustomerInfo(
                booking.getCustomerName(), booking.getCustomerContact(), booking.getAddress()
        );

        List<String> menuItemIds = booking.getMenu().stream().map(Booking.BookedMenuItem::getId).collect(Collectors.toList());
        Map<String, MenuItem> menuItemsMap = menuItemRepository.findAllById(menuItemIds).stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        double ingredientsCost = booking.getMenu().stream()
                .mapToDouble(item -> menuItemsMap.getOrDefault(item.getId(), new MenuItem()).getCost())
                .sum();
        double totalCost = cook.getSessionPrice() + ingredientsCost + (booking.isShopping() ? cook.getShoppingCost() : 0);

        return new CookBookingDetailResponse(
                booking.getId(),
                booking.getDate(),
                booking.getTime(),
                booking.getStatus().name(),
                booking.isShopping(),
                booking.getMenu(),
                customerInfo,
                totalCost
        );
    }

    public Booking updateBookingStatus(String bookingId, String cookUsername, BookingStatus newStatus) {
        User cook = userRepository.findByUsername(cookUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Cook not found: " + cookUsername));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getCookId().equals(cook.getId())) {
            throw new AccessDeniedException("Cook is not authorized to modify this booking");
        }

        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(String bookingId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getCustomerId().equals(user.getId())) {
            throw new AccessDeniedException("User is not authorized to cancel this booking");
        }

        LocalDate today = LocalDate.now(java.util.TimeZone.getTimeZone("UTC").toZoneId());
        if (!booking.getDate().isAfter(today)) {
            throw new IllegalStateException("Bookings from the past or for today cannot be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELED);
        return bookingRepository.save(booking);
    }
}
