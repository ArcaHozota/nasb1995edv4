package app.preach.gospel.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Authority;

/**
 * 権限リポ
 *
 * @author ArkamaHozota
 */
@Repository
public interface AuthorityRepository extends ListCrudRepository<Authority, Long> {
	// 組み込みの findAllById を使用するため、追加の実装は不要です
}
