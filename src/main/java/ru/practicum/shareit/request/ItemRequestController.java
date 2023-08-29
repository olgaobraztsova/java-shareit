package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    // POST /requests
    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestShortDto itemRequestShortDto,
                                            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.createItemRequest(userId, itemRequestShortDto);
    }

    // GET /requests
    @GetMapping
    public List<ItemRequestDtoWithResponses> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequestsByUserId(userId);
    }

    // GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public List<ItemRequestDtoWithResponses> getItemRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) Integer size) {
        return itemRequestService.getItemRequestsByOtherUsers(userId, from, size);
    }

    // GET /requests/{requestId}
    @GetMapping("/{itemRequestId}")
    public ItemRequestDtoWithResponses getItemRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer itemRequestId) {
        return itemRequestService.getItemRequestById(itemRequestId, userId);
    }
}
