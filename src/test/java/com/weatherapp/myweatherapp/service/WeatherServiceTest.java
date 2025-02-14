package com.weatherapp.myweatherapp.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.weatherapp.myweatherapp.exception.BadLocationException;
import com.weatherapp.myweatherapp.exception.WeatherApiException;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private VisualcrossingRepository weatherRepo;

    @InjectMocks
    private WeatherService weatherService;

    private CityInfo oslo;
    private CityInfo stockholm;

    @BeforeEach
    void setUp() {
        oslo = new CityInfo();
        oslo.setAddress("Oslo");
        oslo.getCurrentConditions().setSunrise("05:00:00");
        oslo.getCurrentConditions().setSunset("21:00:00");
        oslo.getCurrentConditions().setConditions("type_21,type_19"); // Raining condition

        stockholm = new CityInfo();
        stockholm.setAddress("Stockholm");
        stockholm.getCurrentConditions().setSunrise("06:00:00");
        stockholm.getCurrentConditions().setSunset("20:00:00");
        stockholm.getCurrentConditions().setConditions("type_41"); // Non-raining condition
    }

    @Test
    void forecastByCity_Success() {
        when(weatherRepo.getByCity("Oslo")).thenReturn(oslo);
        
        CityInfo result = weatherService.forecastByCity("Oslo");
        
        // assert other data is present??
        assertEquals("Oslo", result.getAddress());
        verify(weatherRepo).getByCity("Oslo");
    }

    @Test
    void compareDayLight_ReturnsLongerDaylight() {
        when(weatherRepo.getByCity("Oslo")).thenReturn(oslo);
        when(weatherRepo.getByCity("Stockholm")).thenReturn(stockholm);

        CityInfo result = weatherService.compareDayLight("Oslo", "Stockholm");

        assertEquals("Oslo", result.getAddress());
    }

    @Test
    void compareDayLight_HandlesHttpClientError() {
        when(weatherRepo.getByCity("Oslo"))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.class, () -> 
            weatherService.compareDayLight("Oslo", "Stockholm"));
    }

    @Test
    void compareDayLight_HandlesInvalidTimeFormat() {
        CityInfo invalidCity = new CityInfo();
        invalidCity.getCurrentConditions().setSunrise("invalid");
        invalidCity.getCurrentConditions().setSunset("21:00:00");
        
        when(weatherRepo.getByCity("Oslo")).thenReturn(invalidCity);
        when(weatherRepo.getByCity("Stockholm")).thenReturn(stockholm);

        assertThrows(WeatherApiException.class, () -> 
            weatherService.compareDayLight("Oslo", "Stockholm"));
    }

    @Test
    void compareRain_ReturnsRainingCities() {
        when(weatherRepo.getForecastByCityLangID("Oslo")).thenReturn(oslo);
        when(weatherRepo.getForecastByCityLangID("Stockholm")).thenReturn(stockholm);

        ArrayList<CityInfo> rainingCities = weatherService.compareRain("Oslo", "Stockholm");

        assertEquals(1, rainingCities.size());
        assertEquals("Oslo", rainingCities.get(0).getAddress());
    }

    @Test
    void compareRain_HandlesNoRainingCities() {
        stockholm.getCurrentConditions().setConditions("type_41,type_8");
        CityInfo bergen = new CityInfo();
        bergen.setAddress("Bergen");
        bergen.getCurrentConditions().setConditions("type_43,type_30");

        when(weatherRepo.getForecastByCityLangID("Stockholm")).thenReturn(stockholm);
        when(weatherRepo.getForecastByCityLangID("Bergen")).thenReturn(bergen);

        ArrayList<CityInfo> rainingCities = weatherService.compareRain("Stockholm", "Bergen");

        assertTrue(rainingCities.isEmpty());
    }

    @Test
    void compareRain_HandlesWeatherApiException() {
        when(weatherRepo.getForecastByCityLangID("Oslo"))
            .thenThrow(new WeatherApiException("API Error"));

        assertThrows(WeatherApiException.class, () -> 
            weatherService.compareRain("Oslo", "Stockholm"));
    }

    @Test
    void compareRain_HandlesBadLocationException() {
        when(weatherRepo.getForecastByCityLangID("invalid"))
            .thenThrow(new BadLocationException("API Error"));

        assertThrows(BadLocationException.class, () -> 
            weatherService.compareRain("invalid", "Stockholm"));
    }
}