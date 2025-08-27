package com.flab.auth.service;

import com.flab.auth.dto.LoginResponse;
import com.flab.auth.entity.Status;
import com.flab.auth.entity.User;
import com.flab.auth.jwt.JwtProvider;
import com.flab.auth.repository.UserRepository;
import com.flab.common.exception.ErrorCode;
import com.flab.common.response.BusinessException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final StringRedisTemplate redis;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    // refresh token
    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    /**
     * 구글 계정으로 fluid에 회원가입하는 메서드입니다.
     * @param idToken
     */
    @Transactional
    public void signUpWithGoogle(String idToken) {

        // id token 유효성 검증
        GoogleIdToken.Payload payload = verifyGoogleToken(idToken);

        if (payload == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "구글 토큰이 유효하지 않습니다.");
        }

        String email = payload.getEmail().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 가입된 구글 계정입니다.");
        }

        String name = (String) payload.get("name");

        // 회원 정보 db 저장
        User user = User.builder()
                .email(email)
                .name(name)
                .role("USER")
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    /**
     * 전달받은 idToken이 구글에서 발급한 token이 맞는지 유효성을 검증하는 메서드입니다.
     * @param idToken
     * @return
     */
    private GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), // http 통신 -> 공개키 받아오는 용도
                    GsonFactory.getDefaultInstance() //json 파싱
            ).setAudience(Collections.singletonList(googleClientId)).build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                return null;
            }
            return token.getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 구글 계정으로 fluid에 로그인하는 메서드입니다.
     * @param idToken
     * @return
     */
    @Transactional(readOnly = true)
    public LoginResponse loginWithGoogle(String idToken) {
        GoogleIdToken.Payload payload = verifyGoogleToken(idToken);
        if (payload == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "구글 토큰이 유효하지 않습니다.");
        }

        String email = payload.getEmail().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "가입된 계정 정보가 없습니다. 회원 가입을 먼저 진행해주세요."));

        if (!user.getStatus().canLogin()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "로그인 불가한 사용자입니다.");
        }

        // access Token, refreshToken 발급
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();
        redis.opsForValue().set(REFRESH_PREFIX + refreshToken, user.getId().toString(), REFRESH_TTL);

        return new LoginResponse(accessToken, refreshToken, "로그인에 성공하였습니다.");
    }


    /**
     * 로그아웃 메서드입니다. refresh token을 무효화합니다.
     * @param refreshToken
     */
    @Transactional
    public void logout(String refreshToken){

        String refreshKey = REFRESH_PREFIX + refreshToken;
        redis.delete(refreshKey);
    }

    @Transactional
    public LoginResponse refreshAccessToken(String refreshToken){

        // refresh token 확인
        String userId = redis.opsForValue().get(REFRESH_PREFIX + refreshToken);
        if(userId == null){
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효하지 않거나 만료된 토큰입니다.");
        }

        // 회원 맞는 지 확인
        User user = userRepository.findByIdAndStatus(Long.valueOf(userId), Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND, "현재 활성화된 사용자 정보가 없습니다."));

        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = UUID.randomUUID().toString();

        redis.delete(REFRESH_PREFIX + refreshToken); // 기존 refresh는 제거

        redis.opsForValue().set(REFRESH_PREFIX + newRefreshToken, user.getId().toString(), REFRESH_TTL);

        return new LoginResponse(newAccessToken, newRefreshToken, "토큰이 재발급되었습니다.");
    }


}
