package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.HymnWork;

/**
 * 賛美歌楽譜リポジトリ (Spring Data JDBC)
 *
 * @author ArkamaHozota
 */
@Repository
public interface HymnWorkRepository extends ListCrudRepository<HymnWork, Long> {

	// 楽譜テーブルの全レコード数カウント
	// 対応Dao: HymnWorkDao#countAllRecords
	@Query("SELECT COUNT(1) FROM HYMNS_WORK")
	int countAllRecords();

	// WORK_IDで楽譜を1件取得
	// 対応Dao: HymnWorkDao#selectByWorkId
	// ※Spring Data JDBCの命名規則で自動実装される
	Optional<HymnWork> findByWorkId(Long workId);
}
