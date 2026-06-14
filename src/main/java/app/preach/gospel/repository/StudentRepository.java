package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Student;

/**
 * 奉仕者リポジトリ (Spring Data JDBC)
 *
 * @author ArkamaHozota
 */
@Repository
public interface StudentRepository extends ListCrudRepository<Student, Long> {

	// 有効かつログインアカウントに一致する件数を取得（重複チェック用）
	// 対応Dao: StudentDao#countByLoginAccountAndVisibleFlgTrue
	@Query("SELECT COUNT(1) FROM STUDENTS ST WHERE ST.VISIBLE_FLG = 'true' AND ST.LOGIN_ACCOUNT = :loginAccount")
	int countByLoginAccountAndVisibleFlgTrue(@Param("loginAccount") String loginAccount);

	// 有効かつログインアカウントに一致し、指定IDを除く件数を取得（重複チェック用）
	// 対応Dao: StudentDao#countByLoginAccountAndVisibleFlgTrueAndIdNot
	@Query("SELECT COUNT(1) FROM STUDENTS ST WHERE ST.VISIBLE_FLG = 'true' AND ST.ID <> :id AND ST.LOGIN_ACCOUNT = :loginAccount")
	int countByLoginAccountAndVisibleFlgTrueAndIdNot(@Param("id") Long id, @Param("loginAccount") String loginAccount);

	// アカウント名またはメールアドレスで有効な奉仕者を検索
	// 対応Dao: StudentDao#selectActiveByUsernameOrEmail
	@Query("SELECT ST.ID, ST.LOGIN_ACCOUNT, ST.PASSWORD, ST.USERNAME, ST.DATE_OF_BIRTH, ST.EMAIL, ST.ROLE_ID, ST.UPDATED_TIME, ST.VISIBLE_FLG"
			+ " FROM STUDENTS ST WHERE ST.VISIBLE_FLG = 'true' AND (ST.LOGIN_ACCOUNT = :username OR ST.EMAIL = :username)")
	Optional<Student> findActiveByUsernameOrEmail(@Param("username") String username);

	// IDで有効な奉仕者を1件取得
	// 対応Dao: StudentDao#selectByIdAndVisibleFlgTrue
	@Query("SELECT ST.ID, ST.LOGIN_ACCOUNT, ST.PASSWORD, ST.USERNAME, ST.DATE_OF_BIRTH, ST.EMAIL, ST.ROLE_ID, ST.UPDATED_TIME, ST.VISIBLE_FLG"
			+ " FROM STUDENTS ST WHERE ST.VISIBLE_FLG = 'true' AND ST.ID = :id")
	Optional<Student> findByIdAndVisibleFlgTrue(@Param("id") Long id);

	// ログインアカウントで有効な奉仕者を1件取得
	// 対応Dao: StudentDao#selectByVisibleFlgTrueAndLoginAccount
	@Query("SELECT ST.ID, ST.LOGIN_ACCOUNT, ST.PASSWORD, ST.USERNAME, ST.DATE_OF_BIRTH, ST.EMAIL, ST.ROLE_ID, ST.UPDATED_TIME, ST.VISIBLE_FLG"
			+ " FROM STUDENTS ST WHERE ST.VISIBLE_FLG = 'true' AND ST.LOGIN_ACCOUNT = :loginAccount")
	Optional<Student> findByVisibleFlgTrueAndLoginAccount(@Param("loginAccount") String loginAccount);

	// 奉仕者を論理削除（VISIBLE_FLG = 'false'）
	// 対応Dao: StudentDao#deleteLogically
	@Modifying
	@Query("UPDATE STUDENTS SET VISIBLE_FLG = 'false' WHERE ID = :id")
	void deleteLogically(@Param("id") Long id);
}
