package app.preach.gospel.repository;

import org.springframework.data.repository.ListCrudRepository;
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
}
