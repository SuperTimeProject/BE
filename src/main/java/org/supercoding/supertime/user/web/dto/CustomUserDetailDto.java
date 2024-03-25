package org.supercoding.supertime.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.supercoding.supertime.golbal.auth.token.web.dto.TokenUserDto;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Getter
@AllArgsConstructor
public class CustomUserDetailDto implements UserDetails {
    @Delegate
    private TokenUserDto userDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        String role = userDto.getRoles();

        if(role != null){
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return userDto.getUserPassword();
    }

    @Override
    public String getUsername() {
        return userDto.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
