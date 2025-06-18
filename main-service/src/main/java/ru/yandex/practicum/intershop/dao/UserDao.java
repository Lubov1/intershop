package ru.yandex.practicum.intershop.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import ru.yandex.practicum.intershop.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("USERS")
public class UserDao {
    @Id
    private Long id;
    private String username;
    private String password;
    private String authority;

    public UserDao(String username, String password, String authorities) {
        this.username = username;
        this.password = password;
        this.authority = authorities;
    }
    public UserDao(UserDto userDto) {
        this.username = userDto.username();
        this.password = userDto.password();
    }
}
