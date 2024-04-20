package org.supercoding.supertime.golbal.web.entity.dataLoader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.golbal.web.enums.Part;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateDefaultAdmin implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
    }

    @Value("${spring.default.admin_id}")
    private String defaultUserId;
    @Value("${spring.default.admin_pwd}")
    private String defaultPassword;

    private void createDefaultAdmin(){
        final String defaultUserName = "관리자";
        final String defaultUserNickName = "관리자";

        if(!userRepository.existsByUserId(defaultUserId)){
            UserEntity defaultUser = UserEntity.builder()
                    .userId(defaultUserId)
                    .userPassword(passwordEncoder.encode(defaultPassword))
                    .userName(defaultUserName)
                    .userNickname(defaultUserNickName)
                    .roles(Roles.ROLE_ADMIN)
                    .part(Part.PART_UNDEFINED)
                    .isDeleted(0)
                    .verified(Verified.COMPLETED)
                    .build();

            log.info("[CREATE_DEFAULT_ADMIN] 관리자 계정이 생성되었습니다.");

            userRepository.save(defaultUser);
        } else {
            log.info("[CREATE_DEFAULT_ADMIN] 이미 관리자 계정이 존재하여 생성하지 않았습니다.");
        }

    }
}
