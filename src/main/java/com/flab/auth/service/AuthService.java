package com.flab.auth.service;

import com.flab.auth.dto.EmailVerifyResponse;
import com.flab.auth.dto.LoginRequest;
import com.flab.auth.dto.LoginResponse;
import com.flab.auth.dto.SignUpRequest;
import com.flab.auth.entity.Status;
import com.flab.auth.entity.User;
import com.flab.auth.jwt.JwtProvider;
import com.flab.auth.repository.UserRepository;
import com.flab.common.response.BusinessException;
import com.flab.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StringRedisTemplate redis;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // 이메일 인증코드
    private static final String EMAIL_CODE_PREFIX = "auth:email:code:";
    private static final Duration EMAIL_CODE_TTL = Duration.ofMinutes(5);

    // 재전송 제한 키
    private static final String THROTTLE_PREFIX = "auth:email:throttle:";
    private static final Duration THROTTLE_TTL = Duration.ofSeconds(60);

    // 이메일 인증 여부 확인 token
    private static final String EMAIL_VERIFIED_PREFIX = "auth:email:verified:";
    private static final Duration EMAIL_VERIFIED_TTL = Duration.ofMinutes(15);

    // refresh token
    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    /**
     * 사용자 메일로 인증코드를 전송하는 메서드입니다.
     * @param rawEmail
     */
    public void sendEmailCode(String rawEmail){

        // 1. 60 초 이내 재전송 제한
        String throttleKey = THROTTLE_PREFIX + rawEmail.toLowerCase();
        Boolean firstSend = redis.opsForValue().setIfAbsent(throttleKey, "1", THROTTLE_TTL);
        if (Boolean.FALSE.equals(firstSend)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "메일 전송 요청이 너무 많이 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        // 2. 6자리 인증코드 생성
        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        // 3. Redis 저장 (TTL 5분)
        String codeKey = EMAIL_CODE_PREFIX + rawEmail.toLowerCase();
        redis.opsForValue().set(codeKey, code, EMAIL_CODE_TTL);

        // 3. 메일 발송
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(rawEmail);
            message.setSubject("[Fluid] 이메일 인증 코드");
            message.setFrom("homoludensmz@gmail.com");
            message.setText("""
                    안녕하세요, Fluid 입니다.
                    
                    6자리 인증코드를 5분 이내에 입력해주세요.
                    인증코드 : [%s]
                    """.formatted(code));

            mailSender.send(message);
        }catch (Exception e){
            redis.delete(throttleKey); //  60초 제한 풀어주기
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "인증 코드 메일 발송이 실패했습니다.");
        }

    }

    /**
     * 이메일 인증 코드를 검증하는 메서드입니다.
     * @param rawEmail
     * @param code
     * @return
     */
    public EmailVerifyResponse verifyEmailCode(String rawEmail, String code){

        String email = rawEmail.toLowerCase();

        String codeKey = EMAIL_CODE_PREFIX + email;
        String savedValue = redis.opsForValue().get(codeKey);

        if (savedValue == null){
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "인증코드가 만료되었거나 존재하지 않습니다.");
        }

        // 실패
        if(!savedValue.equals(code)){
            // TODO : 실패 카운팅해서 너무 자주 실패 시, block 하는 로직이 필요할까?

           throw new BusinessException(ErrorCode.VALIDATION_ERROR, "인증 코드가 일치하지 않습니다.");
        }

        // 성공
        redis.delete(codeKey); // 인증 코드 즉시 삭제

        // TODO : 이메일 인증 여부 확인과 같은 단기 토큰은 JWT 쓸 필요 없다고 생각
        String emailVerificationToken = UUID.randomUUID().toString();
        String tokenKey = EMAIL_VERIFIED_PREFIX + emailVerificationToken;
        redis.opsForValue().set(tokenKey, email, EMAIL_VERIFIED_TTL);

        return new EmailVerifyResponse(emailVerificationToken);

    }

    /**
     * 회원 가입 직전, 이메일 인증이 올바르게 완료되었는지 확인하는 메서드입니다.
     * @param rawEmail
     * @param token
     */
    public void assertEmailVerifiedToken(String rawEmail, String token){
        String email = rawEmail.toLowerCase();

        String tokenKey = EMAIL_VERIFIED_PREFIX + token;
        String savedValue = redis.opsForValue().get(tokenKey);

        if(savedValue == null){
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "이메일 인증이 완료되지 않았거나, 인증 후 15분이 지나 재인증 받아야 합니다.");
        }

        if (!savedValue.equals(email)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "이메일 인증 토큰이 유효하지 않습니다.");
        }

        redis.delete(tokenKey);
    }

    /**
     * 사용자의 회원가입을 진행하는 메서드입니다.
     * @param request
     */
    @Transactional
    public void registerUser(SignUpRequest request){

        String email = request.getEmail().toLowerCase();

        if(userRepository.existsByEmail(email)){
            throw new BusinessException(ErrorCode.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        if(userRepository.existsByNickname(request.getNickname())){
            throw new BusinessException(ErrorCode.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(email)
                .nickname(request.getNickname())
                .password(hashedPassword)
                .role("USER")
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    /**
     * 로그인 성공 시, access Token과 refresh token을 발급해주는 메서드입니다.
     * @param request
     * @return
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request){

        String email = request.getEmail().toLowerCase();

        // 이메일 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 이메일 입니다."));

        // 차단 or 탈퇴한 사용자인지 검사
        if(!user.getStatus().canLogin()){
            throw new BusinessException(ErrorCode.FORBIDDEN, "현재 로그인 불가능한 사용자입니다.");
        }

        // 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString(); // TODO : 단순 UUID 로 발급했는데 적합한지?

        String refreshKey = REFRESH_PREFIX + refreshToken; // TODO : 추후, 여러 기기 동시 로그인 지원을 위해, refreshToken 자체를 키로 썼는데 괜찮나?
        redis.opsForValue().set(refreshKey, user.getId().toString(), REFRESH_TTL);

        return new LoginResponse(accessToken, refreshToken, "로그인에 성공했습니다.");

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

}
