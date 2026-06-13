package app.preach.gospel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 奉仕者テーブル
 *
 * @author ArkamaHozota
 */
public record Student(Long id, String loginAccount, String password, String username,
		LocalDate dateOfBirth, // DATE型
		String email, Long roleId, LocalDateTime updatedTime, String visibleFlg) {
}
