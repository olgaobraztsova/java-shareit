package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.Status;


import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@ToString
public class BookingDto {

    private Integer id;

    @NotNull
    private Integer itemId;
    private Integer bookerId;

    @FutureOrPresent(message = "Дата начала бронирования не может быть раньше текущей даты")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDateTime start;

    @Future(message = "Дата окончания бронирования не может быть раньше текущей даты")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDateTime end;

    private Status status;
}
