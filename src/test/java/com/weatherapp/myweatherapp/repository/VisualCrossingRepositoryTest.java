package com.weatherapp.myweatherapp.repository;

import com.weatherapp.myweatherapp.model.CityInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class VisualcrossingRepositoryTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VisualcrossingRepository repository;

    private final String TEST_URL = "http://test.url/";
    private final String TEST_KEY = "test-key";
    private final String TEST_CITY = "London";

    private CityInfo expectedCityInfo = new CityInfo();
    

    @BeforeEach
    void setUp() {
        // Set the private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(repository, "url", TEST_URL);
        ReflectionTestUtils.setField(repository, "key", TEST_KEY);
        // Replace the auto-created RestTemplate with our mock
        ReflectionTestUtils.setField(repository, "restTemplate", restTemplate);
        expectedCityInfo.setAddress(TEST_CITY);
        expectedCityInfo.setDescription("Warming up with no rain expected.");
        expectedCityInfo.getCurrentConditions().setConditions("Overcast");
    }

    @Test
    void getByCity_Success() {
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + TEST_CITY + "?key=" + TEST_KEY;
        
        when(restTemplate.getForEntity(expectedUri, CityInfo.class))
            .thenReturn(new ResponseEntity<>(expectedCityInfo, HttpStatus.OK));

        // Act
        CityInfo result = repository.getByCity(TEST_CITY);

        // Assert
        assertNotNull(result);
        assertEquals(expectedCityInfo.getAddress(), result.getAddress());
        assertEquals(expectedCityInfo.getDescription(), result.getDescription());
        assertEquals(expectedCityInfo.getCurrentConditions().getConditions(), 
                    result.getCurrentConditions().getConditions());
        // Assert that URI was called exactly once
        verify(restTemplate).getForEntity(expectedUri, CityInfo.class);
    }

    @Test
    void getByCity_NullCity() {
        assertThrows(NullPointerException.class, () -> {
            repository.getByCity(null);
        });
    }

    @Test
    void getByCity_EmptyCity() {
        assertThrows(NullPointerException.class, () -> {
            repository.getByCity("");
        });
    }

    @Test
    void getByCity_BadRequest() {
        String city = "invalid";
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + city + "?key=" + TEST_KEY;
        
        when(restTemplate.getForEntity(expectedUri, CityInfo.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> {
            repository.getByCity(city);
        });
    }

    @Test
    void getByCity_Unauthorized() {
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + TEST_CITY + "?key=" + TEST_KEY;
        
        when(restTemplate.getForEntity(expectedUri, CityInfo.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> {
            repository.getByCity(TEST_CITY);
        });
    }

    @Test
    void getByCity_TooManyRequests() {
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + TEST_CITY + "?key=" + TEST_KEY;
        
        when(restTemplate.getForEntity(expectedUri, CityInfo.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests"));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> {
            repository.getByCity(TEST_CITY);
        });
    }

    @Test
    void getByCity_InternalServerError() {
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + TEST_CITY + "?key=" + TEST_KEY;
        
        when(restTemplate.getForEntity(expectedUri, CityInfo.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> {
            repository.getByCity(TEST_CITY);
        });
    }

    @Test
    void getByCity_RestClientException() {
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + TEST_CITY + "?key=" + TEST_KEY;
        
        // Simulate RestClientException thrown by RestTemplate
        RestClientException restClientException = new RestClientException("API failure");
        when(restTemplate.getForEntity(expectedUri, CityInfo.class)).thenThrow(restClientException);

        // When & Then
        assertThrows(
            RestClientException.class,
            () -> repository.getByCity(TEST_CITY)  // Call the method under test
        );
    }

    @Test
    void getForecastByCityLangID_Success() {
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + TEST_CITY + "?key=" + TEST_KEY + "&lang=id";
        
        when(restTemplate.getForEntity(expectedUri, CityInfo.class))
            .thenReturn(new ResponseEntity<>(expectedCityInfo, HttpStatus.OK));

        // Act
        CityInfo result = repository.getForecastByCityLangID(TEST_CITY);

        // Assert
        assertNotNull(result);
        assertEquals(expectedCityInfo, result);
        assertNotNull(result);
        assertEquals(expectedCityInfo.getAddress(), result.getAddress());
        assertEquals(expectedCityInfo.getDescription(), result.getDescription());
        assertEquals(expectedCityInfo.getCurrentConditions().getConditions(), 
                    result.getCurrentConditions().getConditions());
        // Assert that URI was called exactly once
        verify(restTemplate, times(1)).getForEntity(expectedUri, CityInfo.class);
    }

    @Test
    void getForecastByCityLangID_NullCity() {
        assertThrows(NullPointerException.class, () -> {
            repository.getForecastByCityLangID(null);
        });
    }

    @Test
    void getForecastByCityLangID_EmptyCity() {
        assertThrows(NullPointerException.class, () -> {
            repository.getForecastByCityLangID("");
        });
    }

    @Test
    void getForecastByCityLangID_Unauthorized() {
        // Arrange
        String expectedUri = TEST_URL + "timeline/" + TEST_CITY + "?key=" + TEST_KEY + "&lang=id";
        
        when(restTemplate.getForEntity(expectedUri, CityInfo.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid API key"));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> {
            repository.getForecastByCityLangID(TEST_CITY);
        });
    }
}