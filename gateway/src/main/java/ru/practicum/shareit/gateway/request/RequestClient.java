package ru.practicum.shareit.gateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.request.dto.RequestDto;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(RequestDto requestDto, Long id) {
        return post("", id, requestDto);
    }

    public ResponseEntity<Object> getByUserId(Long id) {
        return get("", id);
    }

    public ResponseEntity<Object> getListAllRequest(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getByRequestId(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}