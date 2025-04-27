package price.tracker.dto;

import lombok.Getter;

@Getter
public class PasswordResetDTO {
    private String resetCode;
    private String newPassword;

    public PasswordResetDTO(String resetCode, String newPassword) {
        this.resetCode = resetCode;
        this.newPassword = newPassword;
    }
}
