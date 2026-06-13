package app.preach.gospel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 奉仕者テーブル
 *
 * @author ArkamaHozota
 */
@Table("STUDENTS")
public record Student(@Id @Column("ID") Long id, @Column("LOGIN_ACCOUNT") String loginAccount,
		@Column("PASSWORD") String password, @Column("USERNAME") String username,
		@Column("DATE_OF_BIRTH") LocalDate dateOfBirth, // date型
		@Column("EMAIL") String email, @Column("ROLE_ID") Long roleId,
		@Column("UPDATED_TIME") LocalDateTime updatedTime, @Column("VISIBLE_FLG") Boolean visibleFlg) {
}
