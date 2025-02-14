package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.exception.WeatherApiException;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class WeatherService {

  @Autowired
  VisualcrossingRepository weatherRepo;

  // All raining conditions from docs. See https://www.visualcrossing.com/resources/documentation/weather-api/weather-condition-fields/
  private List<String> rainingConditions = Arrays.asList("type_10", "type_11", "type_13", "type_14", "type_21", "type_22", "type_23", "type_24", "type_25", "type_26", "type_32", "type_5", "type_6", "type_9");

  public CityInfo forecastByCity(String city) {

    return weatherRepo.getByCity(city);
  }

  public CityInfo compareDayLight(String city1, String city2) {
    CityInfo cityInfo1;
    CityInfo cityInfo2;
    try {
      cityInfo1 = weatherRepo.getByCity(city1);
      cityInfo2 = weatherRepo.getByCity(city2);
    } catch (HttpClientErrorException ex) {
      // Let HTTP-specific exceptions (like 400, 429, etc.) propagate up
      throw ex;
    } catch (WeatherApiException ex) {
      // Wrap other REST client exceptions
      throw ex;
    }
    
    try {
      LocalTime sunrise1 = LocalTime.parse(cityInfo1.getSunrise());
      LocalTime sunset1 = LocalTime.parse(cityInfo1.getSunset());
      Duration duration1 = Duration.between(sunrise1, sunset1);

      LocalTime sunrise2 = LocalTime.parse(cityInfo2.getSunrise());
      LocalTime sunset2 = LocalTime.parse(cityInfo2.getSunset());
      Duration duration2 = Duration.between(sunrise2, sunset2);
      
      // debugging
      System.out.println("duration1: " + duration1.toString() + " duration2: " + duration2.toString());
      System.out.println(duration1.compareTo(duration2) > 0 ? cityInfo1.getAddress() : cityInfo2.getAddress());
      return duration1.compareTo(duration2) > 0 ? cityInfo1 : cityInfo2;
    } catch (DateTimeParseException e) {
      throw new WeatherApiException("Invalid sunrise/sunset time format in response", e);
    }
  }

  public ArrayList<CityInfo> compareRain(String city1, String city2) {
    CityInfo[] cityInfos = new CityInfo[2];
    try {
      
      cityInfos[0] = weatherRepo.getForecastByCityLangID(city1);
      cityInfos[1] = weatherRepo.getForecastByCityLangID(city2);
    } catch (HttpClientErrorException ex) {
      // Let HTTP-specific exceptions (like 400, 429, etc.) propagate up
      throw ex;
    } catch (WeatherApiException ex) {
      // Wrap other REST client exceptions
      throw ex;
    }

    ArrayList<CityInfo> rainingCities = new ArrayList<CityInfo>();

    for (int i = 0; i < cityInfos.length; i++) {
      String[] conditions = cityInfos[i].getCurrentConditions().split(",");
      System.out.println("city: " + cityInfos[i].getAddress());

      for (String conditionID : conditions) {
        System.out.println("condition: " + conditionID);
        // Only checking for condition "rain", not including various types of rain. See
        // VisualCrossingAPI Docs.
        if (this.rainingConditions.contains(conditionID)) {
          rainingCities.add(cityInfos[i]);
          break;
        }
      }
    }

    System.out.println("Raining Cities: " + rainingCities.toString());
    return rainingCities;
  }
}
