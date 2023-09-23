package ru.practicum.shareit.booking.model;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State getEnumValue(String state) {

        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
