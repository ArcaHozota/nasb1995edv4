package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
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
	@Query("SELECT COUNT(1) FROM HYMNS_WORK")
	int countAllRecords();

	// WORK_IDで楽譜を1件取得
	Optional<HymnWork> findByWorkId(Long workId);

	// 楽譜を1件INSERT
	// ※ SCOREはOracleのBLOB型のため、Spring Data JDBCの@Queryでは
	// byte[]をそのままバインドする(JDBCドライバがBLOBに自動変換)
	// ※ recordのネスト参照(:entity.score)はSpring Data JDBCで未サポートのため
	// 各フィールドを個別@Paramで展開する
	@Modifying
	@Query("INSERT INTO HYMNS_WORK (ID, WORK_ID, SCORE) VALUES (:id, :workId, :score)")
	void insertOne(@Param("id") Long id, @Param("workId") Long workId, @Param("score") byte[] score);
}