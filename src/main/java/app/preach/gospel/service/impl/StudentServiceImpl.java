package app.preach.gospel.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.model.Student;
import app.preach.gospel.repository.StudentRepository;
import app.preach.gospel.service.IStudentService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CoStringUtils;

/**
 * 奉仕者サービス実装クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Service
public class StudentServiceImpl implements IStudentService {

	/**
	 * エンコーダー
	 */
	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(BCryptVersion.$2A, 7);

	/**
	 * 日時フォマーター
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * 奉仕者リポ
	 */
	private final StudentRepository studentRepository;

	/**
	 * コンストラクタ
	 *
	 * @param studentRepository
	 */
	protected StudentServiceImpl(final StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<Integer, DataAccessException> checkDuplicated(final String id, final String loginAccount) {
		try {
			int count;
			if (CoStringUtils.isDigital(id)) {
				count = this.studentRepository.countByLoginAccountAndVisibleFlgTrueAndIdNot(Long.parseLong(id),
						loginAccount);
			} else {
				count = this.studentRepository.countByLoginAccountAndVisibleFlgTrue(loginAccount);
			}
			return CoResult.ok(count);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public CoResult<StudentDto, DataAccessException> getStudentInfoById(final Long id) {
		try {
			final Student student = this.studentRepository.findByIdAndVisibleFlgTrue(id)
					.orElseThrow(() -> new DataAccessException(ProjectConstants.MESSAGE_STUDENT_NOT_FOUND) {
					}); // または適切な例外
			final var dto = new StudentDto(student.id(), student.loginAccount(), student.username(), student.password(),
					student.email(), FORMATTER.format(student.dateOfBirth()), null);
			return CoResult.ok(dto);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

	@Transactional
	@Override
	public CoResult<String, DataAccessException> infoUpdation(final @NotNull StudentDto studentDto) {
		try {
			// 1. 既存データの取得
			final Student existing = this.studentRepository.findByIdAndVisibleFlgTrue(Long.valueOf(studentDto.id()))
					.orElseThrow(() -> new DataAccessException("User not found") {
					});
			// 2. パスワードの判定
			final boolean passwordMatch = CoStringUtils.isEqual(studentDto.password(), existing.password())
					|| ENCODER.matches(studentDto.password(), existing.password());
			// 3. 変更チェック（簡易版：全フィールド比較）
			// ※CoBeanUtils等でマッピング前後のdiffを取ることも可能ですが、レコードならequalsで比較できます
			if (passwordMatch && this.isSameAs(existing, studentDto)) {
				return CoResult.err(new DataRetrievalFailureException(ProjectConstants.MESSAGE_STRING_NO_CHANGE));
			}
			// 4. 更新用レコードの生成（不変オブジェクトのため、新しいインスタンスを作成）
			final var newPassword = passwordMatch ? existing.password() : ENCODER.encode(studentDto.password());
			final var updated = new Student(existing.id(), studentDto.loginAccount(), newPassword,
					studentDto.username(), LocalDate.parse(studentDto.dateOfBirth(), FORMATTER), studentDto.email(),
					existing.roleId(), // 必要に応じて変更
					existing.updatedTime(), // タイムスタンプ維持
					Boolean.TRUE.toString());
			this.studentRepository.save(updated);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED);
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

	// 変更差分チェックのヘルパーメソッド
	private boolean isSameAs(final Student s, final StudentDto dto) {
		return s.loginAccount().equals(dto.loginAccount()) && s.username().equals(dto.username())
				&& s.email().equals(dto.email()) && s.dateOfBirth().format(FORMATTER).equals(dto.dateOfBirth());
	}

	@Transactional
	@Override
	public CoResult<String, Object> preLoginUpdate(final String loginAccount) {
		try {
			return this.studentRepository.findByVisibleFlgTrueAndLoginAccount(loginAccount).map(student -> {
				// 不変レコードのコピーを作成して更新時間をセット
				final var updated = new Student(student.id(), student.loginAccount(), student.password(),
						student.username(), student.dateOfBirth(), student.email(), student.roleId(),
						LocalDateTime.now(), student.visibleFlg());
				this.studentRepository.save(updated);
				return CoResult.ok(ProjectConstants.MESSAGE_STRING_LOGIN_SUCCESS);
			}).orElse(CoResult
					.err(new DataRetrievalFailureException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR1)));
		} catch (final DataAccessException e) {
			return CoResult.err(e);
		}
	}

}
