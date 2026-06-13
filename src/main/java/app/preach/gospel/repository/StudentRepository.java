package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Student;

/**
 * 奉仕者リポ
 *
 * @author ArkamaHozota
 */
@Repository
public interface StudentRepository extends ListCrudRepository<Student, Long> {

	// 重複チェック用（IDを指定しない場合）
	@Query("SELECT count(*) FROM STUDENTS WHERE VISIBLE_FLG = 1 AND LOGIN_ACCOUNT = :loginAccount")
	int countByLoginAccountAndVisibleFlgTrue(String loginAccount);

	// 重複チェック用（IDを指定する場合）
	@Query("SELECT count(*) FROM STUDENTS WHERE VISIBLE_FLG = 1 AND ID <> :id AND LOGIN_ACCOUNT = :loginAccount")
	int countByLoginAccountAndVisibleFlgTrueAndIdNot(Long id, String loginAccount);

	/**
	 * アカウント名またはメールアドレスで有効な学生を検索する (Spring Data JDBCがメソッド名から自動的に「WHERE visible_flg =
	 * true AND (login_account = ? OR email = ?)」に類するクエリを生成します)
	 */
	@Query("SELECT * FROM STUDENTS WHERE VISIBLE_FLG = 1 AND (LOGIN_ACCOUNT = :username OR EMAIL = :username)")
	Optional<Student> findActiveUserByUsernameOrEmail(String username);
	// Optional<Student>
	// findByVisibleFlgTrueAndLoginAccountOrVisibleFlgTrueAndEmail(String
	// loginAccount, String email);

	// IDで取得（有効なもののみ）
	@Query("SELECT * FROM STUDENTS WHERE VISIBLE_FLG = 1 AND ID = :id")
	Optional<Student> findByIdAndVisibleFlgTrue(Long id);

	// アカウントで取得
	Optional<Student> findByVisibleFlgTrueAndLoginAccount(String loginAccount);
}
