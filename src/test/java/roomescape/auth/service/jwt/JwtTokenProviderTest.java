package roomescape.auth.service.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.auth.service.TokenProvider;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @DisplayName("페이로드 조회에 성공한다.")
    @Test
    void getPayload() {
        //given
        String email = "dev.chocochip@gmail.com";
        String accessToken = tokenProvider.createAccessToken(email);

        //when
        String payload = tokenProvider.getPayload(accessToken);

        //then
        assertThat(payload).isEqualTo(email);
    }

    @DisplayName("토큰 검증에 성공한다.")
    @Test
    void validateToken() {
        //given
        String email = "dev.chocochip@gmail.com";
        String accessToken = tokenProvider.createAccessToken(email);
        String invalidToken = "sadfdb13tyh1hjkbwfafs";

        //when & then
        assertAll(
                () -> assertThat(tokenProvider.validateToken(accessToken)).isTrue(),
                () -> assertThat(tokenProvider.validateToken(invalidToken)).isFalse()
        );
    }
}
