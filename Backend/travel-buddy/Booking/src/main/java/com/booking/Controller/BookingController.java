package com.booking.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.DTO.ErrorResponse;
import com.booking.Entity.Booking;
import com.booking.Service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) 
    {
            return ResponseEntity.ok(bookingService.createBooking(booking));
    }

    @PutMapping("/update/booking-status/{bookingId}")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable String bookingId,@RequestParam String status) 
    {
       return ResponseEntity.ok(bookingService.updateBookingStatus(bookingId, status));        
       
    }
    @PutMapping("/update/payment-status/{bookingId}")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable String bookingId,@RequestParam String status) 
    {
            Booking booking = bookingService.updatePaymentStatus(bookingId, status);
            return ResponseEntity.ok(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable String bookingId) 
    {
        
            Booking booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(booking);
    }
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getBookingsByHotelId(@PathVariable String hotelId) 
    {
        
            List<Booking> bookings = bookingService.getBookingsByHotelId(hotelId);
            return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUserId(@PathVariable String userId) 
    {
            List<Booking> bookings = bookingService.getBookingsByUserId(userId);
            return ResponseEntity.ok(bookings);
    }
}
