package com.booking.Service;

import java.util.List;

import com.booking.Entity.Booking;

public interface BookingService {
    
    public Booking createBooking(Booking booking);
    public Booking getBookingById(String bookingId);
    public Booking updateBookingStatus(String bookingId,String status);
    public Booking updatePaymentStatus(String bookingId,String status);
    public List<Booking> getBookingsByHotelId(String hotelId);
    public List<Booking> getBookingsByUserId(String userId);
}
