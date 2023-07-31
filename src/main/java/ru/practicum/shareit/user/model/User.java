package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private Integer id;
    @NonNull
    private String name;
    @Email
    private String email;
}
