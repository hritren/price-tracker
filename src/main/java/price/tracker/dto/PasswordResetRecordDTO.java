package price.tracker.dto;

import lombok.Getter;
import java.time.Instant;

@Getter
public class PasswordResetRecordDTO {
    private String email;
    private long expiration;

    public PasswordResetRecordDTO(String email, long expiration) {
        this.email = email;
        this.expiration = expiration;
    }
}
