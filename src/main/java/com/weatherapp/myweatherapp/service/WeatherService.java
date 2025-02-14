package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.exception.BadLocationException;
import com.weatherapp.myweatherapp.exception.WeatherApiException;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.Duration;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class WeatherService {

  @Autowired
  VisualcrossingRepository weatherRepo;

  // All raining conditions from docs. See https://www.visualcrossing.com/resources/documentation/weather-api/weather-condition-fields/
  // Does not include Drizzle (type_2, type_3)
  private List<String> rainingConditions = Arrays.asList("type_10", "type_11", "type_13", "type_14", "type_21", "type_22", "type_23", "type_24", "type_25", "type_26", "type_32", "type_5", "type_6", "type_9");

  public CityInfo forecastByCity(String city) {
    return fetchCityInfo(city, weatherRepo::getByCity);
  }

  public CityInfo compareDayLight(String city1, String city2) {
    CityInfo cityInfo1 = fetchCityInfo(city1, weatherRepo::getByCity);
    CityInfo cityInfo2 = fetchCityInfo(city2, weatherRepo::getByCity);
    
    try {
      LocalTime sunrise1 = LocalTime.parse(cityInfo1.getCurrentConditions().getSunrise());
      LocalTime sunset1 = LocalTime.parse(cityInfo1.getCurrentConditions().getSunset());
      Duration duration1 = Duration.between(sunrise1, sunset1);

      LocalTime sunrise2 = LocalTime.parse(cityInfo2.getCurrentConditions().getSunrise());
      LocalTime sunset2 = LocalTime.parse(cityInfo2.getCurrentConditions().getSunset());
      Duration duration2 = Duration.between(sunrise2, sunset2);
      
      return duration1.compareTo(duration2) > 0 ? cityInfo1 : cityInfo2;
    } catch (DateTimeParseException e) {
      throw new WeatherApiException("Invalid sunrise/sunset time format in response", e);
    }
  }

  public ArrayList<CityInfo> compareRain(String city1, String city2) {
    CityInfo[] cityInfos = new CityInfo[2];
    cityInfos[0] = fetchCityInfo(city1, weatherRepo::getForecastByCityLangID);
    cityInfos[1] = fetchCityInfo(city2, weatherRepo::getForecastByCityLangID);

    ArrayList<CityInfo> rainingCities = new ArrayList<CityInfo>();

    for (int i = 0; i < cityInfos.length; i++) {
      String[] conditions = cityInfos[i].getCurrentConditions().getConditions().split(",");

      for (String conditionID : conditions) {
        // Only checking for condition "rain", not including various types of rain. See
        // VisualCrossingAPI Docs.
        if (this.rainingConditions.contains(conditionID)) {
          rainingCities.add(cityInfos[i]);
          break;
        }
      }
    }

    return rainingCities;
  }

  private CityInfo fetchCityInfo(String city, Function<String, CityInfo> fetchMethod) {
    try {
      return fetchMethod.apply(city);
    } catch (HttpClientErrorException.BadRequest ex) {
      throw new BadLocationException(String.format("Invalid City Name: %s", city));
    } catch (HttpClientErrorException ex) {
      // Let HTTP-specific exceptions (like 400, 429, etc.) propagate up
      throw ex;
    } catch (RestClientException ex) {
      // Wrap other REST client exceptions
      throw new WeatherApiException("Unknown VisualCrossingAPI error", ex);
    }
  }
}
