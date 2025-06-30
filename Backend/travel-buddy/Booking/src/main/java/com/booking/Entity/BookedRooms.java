package com.booking.Entity;


import java.util.Date;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedRooms {
    private String roomId;
    private String roomType;
    private int numberOfRooms;
    private Date checkIn;
    private Date checkOut;
    private int numberOfGuests;
}
