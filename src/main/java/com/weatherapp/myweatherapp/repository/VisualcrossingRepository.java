package com.weatherapp.myweatherapp.repository;

import com.weatherapp.myweatherapp.model.CityInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
public class VisualcrossingRepository {

  @Value("${weather.visualcrossing.url}")
  String url;
  @Value("${weather.visualcrossing.key}")
  String key;

  public CityInfo getByCity(String city) {
    String uri = url + "timeline/" + city + "?key=" + key;
    RestTemplate restTemplate = new RestTemplate();

    try {
      ResponseEntity<CityInfo> response = restTemplate.getForEntity(uri, CityInfo.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody();
      } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
        throw new RuntimeException("Bad Request: Invalid City Name");
      } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
        throw new RuntimeException("Too Many Requests: Rate Limit exceeded.");
      } else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
        throw new RuntimeException("Server Error: The weather service is down. Please try again.");
      } else {
        throw new RuntimeException("Unexpected Error:" + response.getStatusCode());
      }
    } catch (HttpClientErrorException e) { // 4xx errors
      System.err.println("Client Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
    } catch (HttpServerErrorException e) { // 5xx errors
      System.err.println("Server Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
    } catch (ResourceAccessException e) { // Timeouts / Connection issues
      System.err.println("Timeout or Connection Error: " + e.getMessage());
    } catch (RestClientException e) { // General RestTemplate errors
      System.err.println("Request Failed: " + e.getMessage());
    }
    return restTemplate.getForObject(uri, CityInfo.class);

  }

  // Recommended to use lang=id for weather condition parsing:
  // https://www.visualcrossing.com/resources/documentation/weather-api/weather-condition-fields/
  // Switch to WebClient for Asynchronous requests
  public CityInfo getForecastByCityLangID(String city) {
    String uri = url + "timeline/" + city + "?key=" + key + "&lang=id";
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate.getForObject(uri, CityInfo.class);
  }
}
