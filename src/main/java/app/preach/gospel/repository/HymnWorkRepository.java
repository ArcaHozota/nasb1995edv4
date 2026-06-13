package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.HymnWork;

/**
 * 賛美歌楽譜リポ
 *
 * @author ArkamaHozota
 */
@Repository
public interface HymnWorkRepository extends ListCrudRepository<HymnWork, Long> {

	// 楽譜テーブルの全レコード数カウント
	@Query("SELECT COUNT(*) FROM HYMNS_WORK")
	int countAllRecords();

	// WORK_ID（HYMNSのID）を元に楽譜を1件取得
	Optional<HymnWork> findByWorkId(Long workId);
}