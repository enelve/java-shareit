package ru.practicum.shareit.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingStatus;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, BookingDto bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> update(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getAllBookingByUser(Long userId, BookingStatus state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingByUser(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingItemByUser(Long id, BookingStatus state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner/?state={state}&from={from}&size={size}", id, parameters);
    }
}
