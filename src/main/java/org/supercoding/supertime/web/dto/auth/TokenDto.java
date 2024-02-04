package org.supercoding.supertime.web.dto.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

//access Token과 Refresh Token을 담은 정보
@Data
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenDto {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long duration;
}
