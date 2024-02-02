package org.supercoding.supertime.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.auth.TokenUserDto;
import org.supercoding.supertime.web.dto.user.CustomUserDetailDto;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = (UserEntity) userRepository.findByUserId(email)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 유저 정보가 존재하지 않습니다. email = " + email));

        TokenUserDto userDto = TokenUserDto.toDto(user);

        return new CustomUserDetailDto(userDto);
    }
}
