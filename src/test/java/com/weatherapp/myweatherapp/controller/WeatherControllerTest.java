package com.weatherapp.myweatherapp.controller;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import com.weatherapp.myweatherapp.exception.BadLocationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;


@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void shouldReturnCityInfo_whenValidCityIsProvided() throws Exception {
        CityInfo cityInfo = new CityInfo();
        cityInfo.setAddress("London");
        cityInfo.setDescription("Cooling down");
        cityInfo.getCurrentConditions().setFeelsLike("35.7");
        cityInfo.getCurrentConditions().setSunrise("06:50:53");
        cityInfo.getCurrentConditions().setSunset("17:30:08");
        

        when(weatherService.forecastByCity("London")).thenReturn(cityInfo);

        mockMvc.perform(get("/forecast/London"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address").value("London"))
            .andExpect(jsonPath("$.description").value("Cooling down"))
            .andExpect(jsonPath("$.currentConditions.feelsLike").value("35.7"))
            .andExpect(jsonPath("$.currentConditions.sunrise").value("06:50:53"))
            .andExpect(jsonPath("$.currentConditions.sunset").value("17:30:08"));
            // .andExpect(jsonPath("$.address").value("London"));
            // .andExpect(jsonPath("$.address").value("London"));

    }

    @Test
    void shouldReturnBadRequest_whenInvalidCityIsProvided() throws Exception {
        String city = "invalidCity";
        when(weatherService.forecastByCity(city))
            .thenThrow(new BadLocationException(String.format("Invalid City Name: %s", city)));

        mockMvc.perform(get("/forecast/" + city))
            .andExpect(status().isBadRequest()) // or any custom status you defined
            .andExpect(jsonPath("$.message").value("Weather service error"))
            .andExpect(jsonPath("$.details").value(String.format("Invalid City Name: %s", city)));
    }

    @Test
    void shouldReturnLongestDayCityInfo_whenValidCityIsProvided() throws Exception {
        CityInfo cityInfo = new CityInfo();
        cityInfo.setAddress("London");
        cityInfo.setDescription("Cooling down");
        cityInfo.getCurrentConditions().setFeelsLike("35.7");
        cityInfo.getCurrentConditions().setSunrise("06:50:53");
        cityInfo.getCurrentConditions().setSunset("17:30:08");

        CityInfo cityInfo2 = new CityInfo();
        cityInfo2.setAddress("Oslo");
        cityInfo2.setDescription("Freezing right now");
        cityInfo2.getCurrentConditions().setFeelsLike("35.7");
        cityInfo2.getCurrentConditions().setSunrise("06:50:53");
        cityInfo2.getCurrentConditions().setSunset("19:30:08");
        

        when(weatherService.compareDayLight("London", "Oslo")).thenReturn(cityInfo2);

        mockMvc.perform(get("/compare-daylight/London/Oslo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address").value("Oslo"))
            .andExpect(jsonPath("$.description").value("Freezing right now"))
            .andExpect(jsonPath("$.currentConditions.feelsLike").value("35.7"))
            .andExpect(jsonPath("$.currentConditions.sunrise").value("06:50:53"))
            .andExpect(jsonPath("$.currentConditions.sunset").value("19:30:08"));
            // .andExpect(jsonPath("$.address").value("London"));
            // .andExpect(jsonPath("$.address").value("London"));

    }

    @Test
    void shouldReturnBadRequest_whenInvalidCityIsProvidedDaylight() throws Exception {
        String city = "invalidCity";
        String city2 = "London";
        when(weatherService.compareDayLight(city, city2))
            .thenThrow(new BadLocationException(String.format("Invalid City Name: %s", city)));

        mockMvc.perform(get("/compare-daylight/" + city + "/" + city2))
            .andExpect(status().isBadRequest()) // or any custom status you defined
            .andExpect(jsonPath("$.message").value("Weather service error"))
            .andExpect(jsonPath("$.details").value(String.format("Invalid City Name: %s", city)));
    }

    @Test
    void shouldReturnIsRainingCities_whenValidCityIsProvided() throws Exception {
        ArrayList<CityInfo> cityInfos = new ArrayList<CityInfo>();
        CityInfo cityInfo = new CityInfo();
        cityInfo.setAddress("London");
        cityInfo.setDescription("Cooling down");
        cityInfo.getCurrentConditions().setConditions("type_21");
        cityInfo.getCurrentConditions().setSunrise("06:50:53");
        cityInfo.getCurrentConditions().setSunset("17:30:08");
        cityInfos.add(cityInfo);

        cityInfo = new CityInfo();
        cityInfo.setAddress("Oslo");
        cityInfo.setDescription("Freezing right now");
        cityInfo.getCurrentConditions().setFeelsLike("35.7");
        cityInfo.getCurrentConditions().setConditions("type_21");
        cityInfo.getCurrentConditions().setSunrise("06:50:53");
        cityInfo.getCurrentConditions().setSunset("19:30:08");
        cityInfos.add(cityInfo);
        

        when(weatherService.compareRain("London", "Oslo")).thenReturn(cityInfos);

        mockMvc.perform(get("/compare-rain/London/Oslo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].address").value("London"))
            .andExpect(jsonPath("$[0].description").value("Cooling down"))
            .andExpect(jsonPath("$[0].currentConditions.conditions").value("type_21"))
            .andExpect(jsonPath("$[0].currentConditions.sunrise").value("06:50:53"))
            .andExpect(jsonPath("$[0].currentConditions.sunset").value("17:30:08"))
            .andExpect(jsonPath("$[1].address").value("Oslo"))
            .andExpect(jsonPath("$[1].description").value("Freezing right now"))
            .andExpect(jsonPath("$[1].currentConditions.feelsLike").value("35.7"))
            .andExpect(jsonPath("$[1].currentConditions.conditions").value("type_21"))
            .andExpect(jsonPath("$[1].currentConditions.sunrise").value("06:50:53"))
            .andExpect(jsonPath("$[1].currentConditions.sunset").value("19:30:08"));

    }

    @Test
    void shouldReturnBadRequest_whenInvalidCityIsProvidedRain() throws Exception {
        String city = "invalidCity";
        String city2 = "London";
        when(weatherService.compareRain(city, city2))
            .thenThrow(new BadLocationException(String.format("Invalid City Name: %s", city)));

        mockMvc.perform(get("/compare-rain/" + city + "/" + city2))
            .andExpect(status().isBadRequest()) // or any custom status you defined
            .andExpect(jsonPath("$.message").value("Weather service error"))
            .andExpect(jsonPath("$.details").value(String.format("Invalid City Name: %s", city)));
    }
}
