package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Role;

/**
 * 役割リポ
 *
 * @author ArkamaHozota
 */
@Repository
public interface RoleRepository extends ListCrudRepository<Role, Long> {
	// 組み込みの findAllById を使用するため、追加の実装は不要です

	@Query("SELECT * FROM ROLE RO WHERE RO.ID = :id AND RO.VISIBLE_FLG = 'true'")
	Optional<Role> findByIdAndVisibleFlgTrue(@Param("id") Long id);
}
