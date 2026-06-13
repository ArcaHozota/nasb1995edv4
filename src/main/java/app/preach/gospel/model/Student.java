package app.preach.gospel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
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
		@Column("DATE_OF_BIRTH") LocalDate dateOfBirth, @Column("EMAIL") String email, @Column("ROLE_ID") Long roleId,
		@Column("UPDATED_TIME") LocalDateTime updatedTime, @Column("VISIBLE_FLG") String visibleFlg,
		@Transient Boolean isNewEntity) implements Persistable<Long> {

	/**
	 * 新規作成用のコンストラクタ。isNewEntity を true に固定する。
	 */
	public Student(final Long id, final String loginAccount, final String password, final String username,
			final LocalDate dateOfBirth, final String email, final Long roleId, final LocalDateTime updatedTime,
			final String visibleFlg) {
		this(id, loginAccount, password, username, dateOfBirth, email, roleId, updatedTime, visibleFlg, true);
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return Boolean.TRUE.equals(this.isNewEntity);
	}

}