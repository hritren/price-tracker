package price.tracker.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import price.tracker.resetcode.ResetCodeService;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ResetCodeService resetCodeService;

    public void sendPasswordResetCode(String email) {
        String resetCode = resetCodeService.generateResetCode(email);

        sendEmail(email, resetCode);
    }

    private void sendEmail(String email, String resetCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Your password reset code is: " + resetCode +
                "\n\nThis code is valid for 15 minutes.");

        mailSender.send(message);
    }

}
