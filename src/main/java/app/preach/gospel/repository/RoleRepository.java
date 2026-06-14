package app.preach.gospel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Role;

/**
 * 役割リポジトリ (Spring Data JDBC)
 *
 * <p>注意: MyBatis版(RoleDao)では中間テーブル(ROLE_AUTH)の操作を
 * insertRoleAuth / deleteRoleAuthByRoleId / selectRoleAuthByRoleId で直接管理しているが、
 * Spring Data JDBC版では@MappedCollectionによるSet&lt;AuthorityRef&gt;の
 * 集約管理(save()時に自動でROLE_AUTHを同期)を利用する設計となる。
 * そのためmodelのRole.javaにSet&lt;AuthorityRef&gt;と@MappedCollectionの復元が必要。</p>
 *
 * @author ArkamaHozota
 */
@Repository
public interface RoleRepository extends ListCrudRepository<Role, Long> {

	// 全役割をID昇順で取得
	// 対応Dao: RoleDao#selectAll
	List<Role> findAllByOrderByIdAsc();

	// IDで有効な役割を1件取得
	// 対応Dao: RoleDao#selectByIdAndVisibleFlgTrue
	@Query("SELECT RO.ID, RO.NAME, RO.VISIBLE_FLG FROM ROLES RO WHERE RO.ID = :id AND RO.VISIBLE_FLG = 'true'")
	Optional<Role> findByIdAndVisibleFlgTrue(@Param("id") Long id);

	// 役割を論理削除（VISIBLE_FLG = 'false'）
	// ※MyBatis版にはないが、比較用に追加
	@Modifying
	@Query("UPDATE ROLES SET VISIBLE_FLG = 'false' WHERE ID = :id")
	void deleteLogically(@Param("id") Long id);
}
