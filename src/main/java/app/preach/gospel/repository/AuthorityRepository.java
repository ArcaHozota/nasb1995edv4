package app.preach.gospel.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Authority;

/**
 * 権限リポジトリ (Spring Data JDBC)
 *
 * @author ArkamaHozota
 */
@Repository
public interface AuthorityRepository extends ListCrudRepository<Authority, Long> {

	// IDリストで権限を取得
	// 対応Dao: AuthorityDao#selectByIds (foreachはSpring Data命名規則で代替)
	List<Authority> findAllByIdIn(List<Long> ids);

	// 全権限をID昇順で取得
	// 対応Dao: AuthorityDao#selectAll
	List<Authority> findAllByOrderByIdAsc();
}
