package com.weatherapp.myweatherapp.repository;

import com.weatherapp.myweatherapp.exception.*;
import com.weatherapp.myweatherapp.model.CityInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.*;

@Repository
public class VisualcrossingRepository {

    @Value("${weather.visualcrossing.url}")
    private String url;

    @Value("${weather.visualcrossing.key}")
    private String key;

    private final RestTemplate restTemplate;

    // Inject RestTemplate as a Spring Bean
    public VisualcrossingRepository() {
        this.restTemplate = new RestTemplate();
    }

    public CityInfo getByCity(String city) {
        String uri = url + "timeline/" + city + "?key=" + key;
        return fetchCityData(uri, city);
    }

    // Recommended to use lang=id for weather condition parsing:
    // https://www.visualcrossing.com/resources/documentation/weather-api/weather-condition-fields/
    // Switch to WebClient for Asynchronous requests
    public CityInfo getForecastByCityLangID(String city) {
        String uri = url + "timeline/" + city + "?key=" + key + "&lang=id";
        return fetchCityData(uri, city);
    }

    // Extract common error handling logic
    private CityInfo fetchCityData(String uri, String city) {
        try {
            ResponseEntity<CityInfo> response = restTemplate.getForEntity(uri, CityInfo.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            // Let HTTP-specific exceptions (like 400, 429, etc.) propagate up
            throw ex;
        } catch (RestClientException ex) {
            // Wrap other REST client exceptions
            throw new WeatherApiException("Failed to fetch weather data for city: " + city, ex);
        }
    }
}
