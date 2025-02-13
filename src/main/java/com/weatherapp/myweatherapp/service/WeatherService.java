package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;

@Service
public class WeatherService {

  @Autowired
  VisualcrossingRepository weatherRepo;

  public CityInfo forecastByCity(String city) {

    return weatherRepo.getByCity(city);
  }

  public CityInfo compareDayLight(String city1, String city2) {
    CityInfo cityInfo1 = weatherRepo.getByCity(city1);

    LocalTime sunrise = LocalTime.parse(cityInfo1.getSunrise());
    LocalTime sunset = LocalTime.parse(cityInfo1.getSunset());

    Duration duration = Duration.between(sunrise, sunset);

    CityInfo cityInfo2 = weatherRepo.getByCity(city2);
    sunrise = LocalTime.parse(cityInfo2.getSunrise());
    sunset = LocalTime.parse(cityInfo2.getSunset());

    Duration duration2 = Duration.between(sunrise, sunset);

    if (duration.compareTo(duration2) > 0) {
      return cityInfo1;
    } else {
      return cityInfo2;
    }
  }

  public ArrayList<CityInfo> compareRain(String city1, String city2) {
    CityInfo[] cityInfos = new CityInfo[2];
    cityInfos[0] = weatherRepo.getForecastByCityLangID(city1);
    cityInfos[1] = weatherRepo.getForecastByCityLangID(city2);

    ArrayList<CityInfo> rainingCities = new ArrayList<CityInfo>();

    for (int i = 0; i < cityInfos.length; i++) {
      String[] conditions = cityInfos[i].getCurrentConditions().split(",");

      for (String conditionID : conditions) {
        // Only checking for condition "rain", not including various types of rain. See
        // VisualCrossingAPI Docs.
        if (conditionID == "type_21") {
          rainingCities.add(cityInfos[i]);
          break;
        }
      }
    }

    return rainingCities;
  }
}
