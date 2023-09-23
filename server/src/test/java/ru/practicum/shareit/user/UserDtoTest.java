package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = new UserDto(1, "user name", "user@email.com");

        JsonContent<UserDto> jsonContent = json.write(userDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId());
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
