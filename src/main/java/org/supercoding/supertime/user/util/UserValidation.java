package org.supercoding.supertime.user.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.golbal.web.advice.CustomDataIntegerityCiolationException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;

    public UserEntity validateExistUser(String username) {
        return userRepository.findByUserId(username)
                .orElseThrow(()-> new CustomNotFoundException("로그인된 유저가 존재하지 않습니다."));
    }

    public void validateDuplicateNickname(String newNickname) {
        if(userRepository.existsByUserNickname(newNickname)) {
            throw new CustomDataIntegerityCiolationException("이미 사용중인 닉네임입니다.");
        }
    }
}
