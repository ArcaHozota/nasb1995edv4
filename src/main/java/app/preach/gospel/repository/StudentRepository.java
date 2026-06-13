package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
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
	@Query("SELECT count(*) FROM STUDENTS WHERE VISIBLE_FLG = 'true' AND LOGIN_ACCOUNT = :loginAccount")
	int countByLoginAccountAndVisibleFlgTrue(@Param("loginAccount") String loginAccount);

	// 重複チェック用（IDを指定する場合）
	@Query("SELECT count(*) FROM STUDENTS WHERE VISIBLE_FLG = 'true' AND ID <> :id AND LOGIN_ACCOUNT = :loginAccount")
	int countByLoginAccountAndVisibleFlgTrueAndIdNot(@Param("id") Long id, @Param("loginAccount") String loginAccount);

	/**
	 * アカウント名またはメールアドレスで有効な学生を検索する
	 */
	@Query("SELECT * FROM STUDENTS WHERE VISIBLE_FLG = 'true' AND (LOGIN_ACCOUNT = :username OR EMAIL = :username)")
	Optional<Student> findActiveUserByUsernameOrEmail(@Param("username") String username);

	// IDで取得（有効なもののみ）
	@Query("SELECT * FROM STUDENTS ST WHERE ST.VISIBLE_FLG = 'true' AND ST.ID = :id")
	Optional<Student> findByIdAndVisibleFlgTrue(@Param("id") Long id);

	// アカウントで取得
	@Query("SELECT * FROM STUDENTS WHERE VISIBLE_FLG = 'true' AND LOGIN_ACCOUNT = :loginAccount")
	Optional<Student> findByVisibleFlgTrueAndLoginAccount(@Param("loginAccount") String loginAccount);
}