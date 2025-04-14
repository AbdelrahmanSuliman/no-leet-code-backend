package com.example.noleetcode.services;

import com.example.noleetcode.config.AppConfig;
import com.example.noleetcode.config.JwtService;
import com.example.noleetcode.config.SecurityConfig;
import com.example.noleetcode.dto.LoginUserDto;
import com.example.noleetcode.dto.RegisterUserDto;
import com.example.noleetcode.dto.ResetPasswordWithCodeDto;
import com.example.noleetcode.dto.VerifyEmailDto;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.User;
import com.example.noleetcode.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;


@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService mailService;



    public AuthService(UserRepository userRepository, SecurityConfig securityConfig, JwtService jwtService, AppConfig appConfig, BCryptPasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public void register(RegisterUserDto registerUserDto) {
        if(userRepository.findByUsername(registerUserDto.username()).isPresent()) {
            logger.warn("Username {} is already in use", registerUserDto.username());
            throw new ApplicationException("Username already in use", HttpStatus.CONFLICT);
        }

        if(userRepository.findByEmail(registerUserDto.email()).isPresent()) {
            logger.warn("Email {} is already in use", registerUserDto.email());
            throw new ApplicationException("Email already in use", HttpStatus.CONFLICT);
        }

        String encryptedPassword = passwordEncoder.encode(registerUserDto.password());

        // Save the user
        User user = new User(
                registerUserDto.username(),
                registerUserDto.email(),
                encryptedPassword
        );

        String verificationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();;

        ZonedDateTime expiration = ZonedDateTime.now().plusMinutes(30);

        user.setEmailVerificationCode(verificationCode);
        user.setEmailVerificationCodeExpiry(expiration);
        user.setEmailVerified(false);

        userRepository.save(user);

        logger.info("User {} registered, pending verification", user.getUsername());

        String emailSubject = "Verify Your Email Address";

// Construct the HTML email body
        String emailHtmlBody = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>" + emailSubject + "</title>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                "  .container { max-width: 600px; margin: 20px auto; padding: 20px; background-color: #ffffff; border: 1px solid #dddddd; border-radius: 5px; }" +
                "  .header { background-color: #28a745; color: #ffffff; padding: 10px 20px; text-align: center; border-radius: 5px 5px 0 0; }" + // Different header color
                "  .content { padding: 20px; }" +
                "  .code-block { background-color: #e9ecef; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 3px; border-radius: 5px; margin: 20px 0; color: #155724; }" + // Different code color
                "  .footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; padding-top: 10px; border-top: 1px solid #eeeeee; }" +
                "  a { color: #28a745; }" + // Link color matching header
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "  <div class=\"header\">" +
                "    <h1>Email Verification Required</h1>" + // Clear header
                "  </div>" +
                "  <div class=\"content\">" +
                "    <p>Dear " + user.getUsername() + ",</p>" +
                "    <p>Thank you for registering with No-Leet-Code!</p>" +
                "    <p>To complete your registration, please use the following verification code:</p>" +
                "    <div class=\"code-block\">" + verificationCode + "</div>" + // Highlighted code block
                "    <p>This code will expire in <strong>30 minutes</strong>.</p>" + // Bold expiry time
                "    <p>If you did not request this verification, please ignore this email.</p>" +
                "  </div>" +
                "  <div class=\"footer\">" +
                "    <p>Sincerely,<br>The No-Leet-Code Team</p>" + // Consistent closing
                "    <p>&copy; " + java.time.Year.now().getValue() + " No-Leet-Code. All rights reserved.</p>" + // Optional copyright year
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";
        mailService.sendEmail(user.getEmail(), emailSubject, emailHtmlBody);

    }

    public String verifyEmail(VerifyEmailDto verifyEmailDto) {
        User user = userRepository.findByUsername(verifyEmailDto.username())
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
        if (user.isEmailVerified()) {
            throw new ApplicationException("Email already verified", HttpStatus.BAD_REQUEST);
        }

        if (user.getEmailVerificationCode() == null || user.getEmailVerificationCodeExpiry() == null) {
            throw new ApplicationException("Verification code not generated or expired", HttpStatus.BAD_REQUEST);
        }

        if (user.getEmailVerificationCodeExpiry().isBefore(ZonedDateTime.now())) {
            throw new ApplicationException("Verification code expired", HttpStatus.BAD_REQUEST);
        }

        if (!user.getEmailVerificationCode().equals(verifyEmailDto.verificationCode())) {
            throw new ApplicationException("Invalid verification code", HttpStatus.BAD_REQUEST);
        }

        user.setEmailVerified(true);
        user.setEmailVerificationCode(null); // Clear the code
        user.setEmailVerificationCodeExpiry(null); // Clear expiry
        userRepository.save(user);
        logger.info("Email verified successfully for user {}", verifyEmailDto.username());

       return jwtService.generateToken(user);

    }

    public String login(LoginUserDto loginUserDto) {
        User user = userRepository.findByUsername(loginUserDto.username())
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

        if (!user.isEmailVerified()) {
            logger.warn("Login attempt for unverified user {}", loginUserDto.username());
            throw new ApplicationException("Email not verified", HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(loginUserDto.password(), user.getPassword())) {
            logger.warn("User with username {} has incorrect password", loginUserDto.username());
            throw new ApplicationException("Wrong password", HttpStatus.UNAUTHORIZED);
        }

        return jwtService.generateToken(user);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

        String resetCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        ZonedDateTime expiration = ZonedDateTime.now().plusMinutes(15);

        user.setPasswordResetCode(resetCode);
        user.setPasswordResetCodeExpiry(expiration);
        userRepository.save(user);
        logger.info("Password reset code generated for user {}", user.getUsername());

        String emailSubject = "Your Password Reset Code";

// Construct the HTML email body
        String emailHtmlBody = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>" + emailSubject + "</title>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                "  .container { max-width: 600px; margin: 20px auto; padding: 20px; background-color: #ffffff; border: 1px solid #dddddd; border-radius: 5px; }" +
                "  .header { background-color: #007bff; color: #ffffff; padding: 10px 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                "  .content { padding: 20px; }" +
                "  .code-block { background-color: #e9ecef; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 3px; border-radius: 5px; margin: 20px 0; color: #0056b3; }" +
                "  .footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; padding-top: 10px; border-top: 1px solid #eeeeee; }" +
                "  a { color: #007bff; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "  <div class=\"header\">" +
                "    <h1>Password Reset Request</h1>" + // Clear header
                "  </div>" +
                "  <div class=\"content\">" +
                "    <p>Dear " + user.getUsername() + ",</p>" +
                "    <p>We received a request to reset the password for your No-Leet-Code account.</p>" +
                "    <p>Please use the following code to complete the password reset process:</p>" +
                "    <div class=\"code-block\">" + resetCode + "</div>" + // Highlighted code block
                "    <p>This code will expire in <strong>15 minutes</strong>.</p>" + // Bold expiry time
                "    <p>If you did not request a password reset, please ignore this email or contact support if you have concerns.</p>" +
                "  </div>" +
                "  <div class=\"footer\">" +
                "    <p>Sincerely,<br>The No-Leet-Code Team</p>" + // Consistent closing
                "    <p>&copy; " + java.time.Year.now().getValue() + " No-Leet-Code. All rights reserved.</p>" + // Optional copyright year
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";
        mailService.sendEmail(user.getEmail(), emailSubject, emailHtmlBody);
    }

    public void resetPassword(ResetPasswordWithCodeDto resetDto) {
        User user = userRepository.findByEmailAndPasswordResetCode(resetDto.email(), resetDto.code())
                .orElseThrow(() -> new ApplicationException("Invalid email or password reset code", HttpStatus.BAD_REQUEST));

        if (user.getPasswordResetCodeExpiry() == null || user.getPasswordResetCodeExpiry().isBefore(ZonedDateTime.now())) {
            throw new ApplicationException("Password reset code expired", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(resetDto.newPassword()));
        user.setPasswordResetCode(null);
        user.setPasswordResetCodeExpiry(null);
        userRepository.save(user);
        logger.info("Password successfully reset for user {}", user.getUsername());

        mailService.sendEmail(user.getEmail(), "Password Reset Confirmation", "Your password has been successfully reset.");
    }



}
