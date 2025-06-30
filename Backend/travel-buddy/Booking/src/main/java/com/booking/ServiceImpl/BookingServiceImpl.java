package com.booking.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking.Entity.Booking;
import com.booking.Exceptions.ResourceNotFoundException;
import com.booking.Repository.BookingRepository;
import com.booking.Service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepo;

    @Override
    public Booking createBooking(Booking booking) {
            Booking savedBooking =  bookingRepo.save(booking);
            return savedBooking;
    }

    @Override
    public Booking getBookingById(String bookingId) 
    {
       return bookingRepo.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking not found with id: "+bookingId));
    }

    @Transactional
    @Override
    public Booking updateBookingStatus(String bookingId, String status)
    {
            Booking booking =  bookingRepo.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking not found with id: "+bookingId));

            booking.setBookingStatus(status);
            
            return bookingRepo.save(booking);
    }

    @Transactional
    @Override
    public Booking updatePaymentStatus(String bookingId, String status) 
    {
            Booking booking =  bookingRepo.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking not found with id: "+bookingId));
            
             booking.setPaymentStatus(status);
             return bookingRepo.save(booking);
    }

    @Override
    public List<Booking> getBookingsByHotelId(String hotelId) 
    {
        List<Booking>bookings =  bookingRepo.findByHotelId(hotelId);

        if(bookings.isEmpty())
        {
            throw new ResourceNotFoundException("No bookings found for hotel with id: "+hotelId);
        }
        return bookings;
    }

    @Override
    public List<Booking> getBookingsByUserId(String userId) {
        List<Booking> bookings =  bookingRepo.findByUserId(userId);

        if(bookings.isEmpty())
        {
            throw new ResourceNotFoundException("No bookings found for user with id: "+userId);
        }
        return bookings;
    }    
}
