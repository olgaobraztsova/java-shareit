package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;
    private UserDto booker;
    private UserDto owner;
    private BookingDto bookingDto;
    private BookingResponseDto bookingResponseDto;
    private BookingResponseDto approvedBookingResponseDto;


    @BeforeEach
    void setup() {
        mapper.registerModule(new JavaTimeModule());

        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        booker = UserDto.builder()
                .id(1)
                .name("user name 1")
                .email("email1@email.com")
                .build();

        owner = UserDto.builder()
                .id(2)
                .name("owner")
                .email("owner@email.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .ownerId(2)
                .requestId(1)
                .build();

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(3);

        bookingDto = BookingDto.builder()
                .itemId(1)
                .bookerId(1)
                .start(start)
                .end(end)
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        approvedBookingResponseDto = BookingResponseDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void testCreateBooking() throws Exception {
        Mockito.when(bookingService.createBooking(any(), any())).thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start[0]", is(bookingResponseDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingResponseDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingResponseDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingResponseDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingResponseDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingResponseDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.item.ownerId", is(bookingResponseDto.getItem().getOwnerId())))
                .andExpect(jsonPath("$.item.description", is(bookingResponseDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingResponseDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())));

    }

    @Test
    void testGetBookingById() throws Exception {
        Mockito.when(bookingService.getBookingById(any(), any())).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start[0]", is(bookingResponseDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingResponseDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingResponseDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingResponseDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingResponseDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingResponseDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.item.ownerId", is(bookingResponseDto.getItem().getOwnerId())))
                .andExpect(jsonPath("$.item.description", is(bookingResponseDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingResponseDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())));
    }

    @Test
    void testApproveBooking() throws Exception {
        Mockito.when(bookingService.approveBooking(any(), any(), any())).thenReturn(approvedBookingResponseDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Status.APPROVED.name())));
    }

    @Test
    void testGetAllBookingsByUser() throws Exception {
        Mockito.when(bookingService.getAllBookingsByUser(any(), any(), any(), any()))
                .thenReturn(List.of(approvedBookingResponseDto));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(approvedBookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start[0]", is(approvedBookingResponseDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(approvedBookingResponseDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(approvedBookingResponseDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[0]", is(approvedBookingResponseDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(approvedBookingResponseDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(approvedBookingResponseDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].booker.name", is(approvedBookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].booker.email", is(approvedBookingResponseDto.getBooker().getEmail())))
                .andExpect(jsonPath("$[0].item.name", is(approvedBookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$[0].item.ownerId", is(approvedBookingResponseDto.getItem().getOwnerId())))
                .andExpect(jsonPath("$[0].item.description", is(approvedBookingResponseDto.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(approvedBookingResponseDto.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].status", is(approvedBookingResponseDto.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsByUser(1, "ALL", 0, 10);

    }

    @Test
    void testGetAllItemBookingsByOwner() throws Exception {
        Mockito.when(bookingService.getAllItemBookingsByOwner(any(), any(), any(), any()))
                .thenReturn(List.of(approvedBookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(approvedBookingResponseDto))));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllItemBookingsByOwner(1, "ALL", 0, 10);
    }
}
