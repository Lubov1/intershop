package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.yandex.practicum.intershop.dao.UserDao;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(UserDao userDao) {
        username = userDao.getUsername();
        password = userDao.getPassword();
        authorities = List.of(new SimpleGrantedAuthority(userDao.getAuthority()));
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
