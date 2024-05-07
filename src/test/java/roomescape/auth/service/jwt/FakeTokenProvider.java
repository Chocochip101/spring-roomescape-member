package roomescape.auth.service.jwt;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import roomescape.auth.service.TokenProvider;

public class FakeTokenProvider implements TokenProvider {
    Map<String, String> tokens = new HashMap<>();

    @Override
    public String createAccessToken(String payload) {
        String token = createRandomString();
        tokens.put(token, payload);
        return token;
    }

    private String createRandomString() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    @Override
    public String getPayload(String token) {
        return tokens.get(token);
    }

    @Override
    public boolean validateToken(String token) {
        return tokens.containsKey(token);
    }
}
