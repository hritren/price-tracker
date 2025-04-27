package price.tracker.resetcode;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import price.tracker.dto.PasswordResetRecordDTO;
import price.tracker.resetcode.exception.InvalidResetCodeException;

@Service
@RequiredArgsConstructor
public class ResetCodeService {

    private final JdbcTemplate jdbcTemplate;

    public String getEmailFromResetCode(String resetCode) {
        String query = "SELECT email, expiration FROM password_reset_codes WHERE code = ?";

        PasswordResetRecordDTO resetRecord = jdbcTemplate.queryForObject(
                query,
                new Object[]{resetCode},
                (rs, rowNum) -> new PasswordResetRecordDTO(
                        rs.getString("email"),
                        rs.getLong("expiration")
                )
        );

        if (resetRecord == null || resetRecord.getExpiration() < System.currentTimeMillis()) {
            throw new InvalidResetCodeException("Reset code is invalid or has been deleted");
        }

        deleteResetCode(resetCode);

        return resetRecord.getEmail();
    }

    public String generateResetCode(String email) {
        String resetCode = java.util.UUID.randomUUID().toString().substring(0, 6);

        saveResetCode(email, resetCode);

        return resetCode;
    }

    private void saveResetCode(String email, String resetCode) {
        String query = "INSERT INTO password_reset_codes (email, code, expiration) VALUES (?, ?, ?)"
                + "ON DUPLICATE KEY UPDATE code = VALUES(code), expiration = VALUES(expiration)";
        long expirationTime = System.currentTimeMillis() + 15 * 60 * 1000;
        System.out.println(System.currentTimeMillis());
        System.out.println(expirationTime);
        jdbcTemplate.update(query, email, resetCode, expirationTime);
    }

    private void deleteResetCode(String resetCode) {
        String deleteQuery = "DELETE FROM password_reset_codes WHERE code = ?";
        jdbcTemplate.update(deleteQuery, resetCode);
    }

}
