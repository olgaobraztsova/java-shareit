package ru.practicum.shareit.booking.dto;

import java.util.Optional;

/** Класс для описания состояний бронирования */

public enum BookingState {
	/**
	 * 	Состояния бронирований: все, текущие, будущие, завершенные,
	 * 	отклоненные, ожидающие подтверждения
	 */
	ALL,
	CURRENT,
	FUTURE,
	PAST,
	REJECTED,
	WAITING;

	public static Optional<BookingState> from(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}
