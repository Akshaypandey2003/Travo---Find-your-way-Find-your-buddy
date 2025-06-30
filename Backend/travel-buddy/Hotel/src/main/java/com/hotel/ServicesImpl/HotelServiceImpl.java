package com.hotel.ServicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.Entities.Hotel;
import com.hotel.Exceptions.ResourceNotFoundException;
import com.hotel.Repositories.HotelRepo;
import com.hotel.Services.HotelService;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepo hotelRepo;


    @Override
    public Hotel addHotel(Hotel hotel) {
        return hotelRepo.save(hotel);
    }

    @Override
    public List<Hotel> getAllHotels() {
       return hotelRepo.findAll();
    }

    @Override
    public Hotel getHotelById(String hotelId) {
       return hotelRepo.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("No Record found associated with Hotel-Id "+hotelId));
    }

    @Override
    public List<Hotel> getHotelsByCategory(List<String> category) 
    {
      List<Hotel> hotels =  hotelRepo.findByHotelCategoryIn(category);
      if(hotels.isEmpty())
      {
          throw new ResourceNotFoundException("Sorry for inconvenience!! currently we do not have any registered hotel with category"+category);
      }
      return hotels;
    }

    @Override
    public List<Hotel> getHotelsByType(List<String> type) {
       List<Hotel> hotels =  hotelRepo.findByHotelTypeIn(type);
       if(hotels.isEmpty())
       {
           throw new ResourceNotFoundException("Sorry for inconvenience!! currently we do not have any registered hotel with type"+type);
       }
       return hotels;

    }

    @Override
    public List<Hotel> getHotelsByCity(String city) {
      List<Hotel> hotels =  hotelRepo.findByHotelCity(city);
      if(hotels.isEmpty())
      {
          throw new ResourceNotFoundException("Sorry for inconvenience!! currently we do not have any registered hotel in "+city);
      }
      return hotels;
    }

    @Override
    public List<Hotel> getHotelsByState(String state) {
        List<Hotel> hotels =  hotelRepo.findByHotelState(state);

        if(hotels.isEmpty())
        {
            throw new ResourceNotFoundException("Sorry for inconvenience!! currently we do not have any registered hotel in "+state);
        }
        return hotels;
    }
    
}
