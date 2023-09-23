package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    // POST /requests
    @PostMapping
    public  ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestShortDto itemRequestShortDto,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.createItemRequest(userId, itemRequestShortDto);
    }

    // GET /requests
    @GetMapping
    public  ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    // GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) Integer size) {
        return itemRequestClient.getItemRequestsByOtherUsers(userId, from, size);
    }

    // GET /requests/{requestId}
    @GetMapping("/{itemRequestId}")
    public  ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Integer itemRequestId) {
        return itemRequestClient.getItemRequestById(itemRequestId, userId);
    }
}
