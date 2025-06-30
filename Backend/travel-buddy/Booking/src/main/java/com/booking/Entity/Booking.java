package com.booking.Entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Document("Booking")
public class Booking {
    
    @Id
    private String bookingId;
    private String userId;
    private String hotelId;
    private String bookingStatus;
    private String paymentStatus;
    private String paymentId;
    private Date paymentDate;
    private String paymentAmount;
    private String paymentMode;
    private String paymentRef;
    private String invoiceId;
    private List<BookedRooms> bookedRooms;
}
