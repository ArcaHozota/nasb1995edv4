package app.preach.gospel.dao;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import app.preach.gospel.model.Student;

/**
 * 奉仕者Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface StudentDao {

	/**
	 * 有効かつログインアカウントに一致する件数を取得（重複チェック用）
	 */
	int countByLoginAccountAndVisibleFlgTrue(String loginAccount);

	/**
	 * 有効かつログインアカウントに一致し、指定IDを除く件数を取得（重複チェック用）
	 */
	int countByLoginAccountAndVisibleFlgTrueAndIdNot(@Param("id") Long id, @Param("loginAccount") String loginAccount);

	/**
	 * アカウント名またはメールアドレスで有効な奉仕者を検索
	 */
	Optional<Student> selectActiveByUsernameOrEmail(String username);

	/**
	 * IDで有効な奉仕者を1件取得
	 */
	Optional<Student> selectByIdAndVisibleFlgTrue(Long id);

	/**
	 * ログインアカウントで有効な奉仕者を1件取得
	 */
	Optional<Student> selectByVisibleFlgTrueAndLoginAccount(String loginAccount);

	/**
	 * 奉仕者を1件登録
	 */
	int insert(Student student);

	/**
	 * 奉仕者を1件更新
	 */
	int update(Student student);

	/**
	 * 奉仕者を論理削除（VISIBLE_FLG = 'false'）
	 */
	int deleteLogically(Long id);
}
