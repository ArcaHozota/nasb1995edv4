package app.preach.gospel.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import app.preach.gospel.model.Role;

/**
 * 役割Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface RoleDao {

	/**
	 * 全役割を取得
	 */
	List<Role> selectAll();

	/**
	 * IDで1件取得
	 */
	Optional<Role> selectById(Long id);

	/**
	 * IDで有効な役割を1件取得
	 */
	Optional<Role> selectByIdAndVisibleFlgTrue(Long id);

	/**
	 * 役割を1件登録
	 */
	int insert(Role role);

	/**
	 * 役割を1件更新
	 */
	int update(Role role);

	/**
	 * 中間テーブル(ROLE_AUTH)に権限を追加
	 */
	int insertRoleAuth(Long roleId, Long authId);

	/**
	 * 中間テーブル(ROLE_AUTH)から指定役割の権限を全削除
	 */
	int deleteRoleAuthByRoleId(Long roleId);
}
