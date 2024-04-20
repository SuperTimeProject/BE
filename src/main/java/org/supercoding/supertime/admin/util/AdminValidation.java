package org.supercoding.supertime.admin.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.golbal.web.advice.CustomAccessDeniedException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@RequiredArgsConstructor
@Component
public class AdminValidation {
    private final UserRepository userRepository;

    public void validateAdminRole(User user) {
        UserEntity targetUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("유저가 존재하지 않습니다."));

        if(targetUser.getRoles() != Roles.ROLE_ADMIN) {
            throw new CustomAccessDeniedException("관지자 권한이 없습니다.");
        };
    }

    public UserEntity findUserEntity(long userCid) {
        return userRepository.findByUserCid(userCid)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));
    }
}
