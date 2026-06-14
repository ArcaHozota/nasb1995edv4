package app.preach.gospel.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Hymn;

/**
 * 賛美歌リポジトリ (Spring Data JDBC)
 *
 * @author ArkamaHozota
 */
@Repository
public interface HymnRepository extends ListCrudRepository<Hymn, Long> {

	// 有効な賛美歌の全件数を取得
	// 対応Dao: HymnDao#countByVisibleFlgTrue
	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true'")
	long countByVisibleFlgTrue();

	// 有効かつ名前(JP)に一致する件数を取得
	// 対応Dao: HymnDao#countByVisibleFlgTrueAndNameJp
	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_JP = :nameJp")
	int countByVisibleFlgTrueAndNameJp(@Param("nameJp") String nameJp);

	// 有効かつ名前(JP)に一致し、指定IDを除く件数を取得（重複チェック用）
	// 対応Dao: HymnDao#countByVisibleFlgTrueAndNameJpAndIdNot
	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_JP = :nameJp AND HM.ID <> :id")
	int countByVisibleFlgTrueAndNameJpAndIdNot(@Param("nameJp") String nameJp, @Param("id") Long id);

	// 有効かつ名前(KR)に一致する件数を取得
	// 対応Dao: HymnDao#countByVisibleFlgTrueAndNameKr
	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_KR = :nameKr")
	int countByVisibleFlgTrueAndNameKr(@Param("nameKr") String nameKr);

	// 有効かつ名前(KR)に一致し、指定IDを除く件数を取得（重複チェック用）
	// 対応Dao: HymnDao#countByVisibleFlgTrueAndNameKrAndIdNot
	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_KR = :nameKr AND HM.ID <> :id")
	int countByVisibleFlgTrueAndNameKrAndIdNot(@Param("nameKr") String nameKr, @Param("id") Long id);

	// 有効な賛美歌をNAME_KRでLIKE検索、ID昇順
	// 対応Dao: HymnDao#selectActiveByNameKrLike
	@Query("SELECT HM.ID, HM.NAME_JP, HM.NAME_KR, HM.LINK, HM.UPDATED_TIME, HM.UPDATED_USER, HM.LYRIC, HM.VISIBLE_FLG, HM.CLASSICAL"
			+ " FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_KR LIKE :keyword ORDER BY HM.ID ASC")
	List<Hymn> findActiveByNameKrLike(@Param("keyword") String keyword);

	// 有効かつクラシックではない賛美歌をNAME_KRでLIKE検索、ID昇順
	// 対応Dao: HymnDao#selectActiveByNameKrLikeAndClassicalFalse
	@Query("SELECT HM.ID, HM.NAME_JP, HM.NAME_KR, HM.LINK, HM.UPDATED_TIME, HM.UPDATED_USER, HM.LYRIC, HM.VISIBLE_FLG, HM.CLASSICAL"
			+ " FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.CLASSICAL = 'false' AND HM.NAME_KR LIKE :keyword ORDER BY HM.ID ASC")
	List<Hymn> findActiveByNameKrLikeAndClassicalFalse(@Param("keyword") String keyword);

	// IDで有効な賛美歌を1件取得
	// 対応Dao: HymnDao#selectByIdAndVisibleFlgTrue
	@Query("SELECT HM.ID, HM.NAME_JP, HM.NAME_KR, HM.LINK, HM.UPDATED_TIME, HM.UPDATED_USER, HM.LYRIC, HM.VISIBLE_FLG, HM.CLASSICAL"
			+ " FROM HYMNS HM WHERE HM.ID = :id AND HM.VISIBLE_FLG = 'true'")
	Optional<Hymn> findByIdAndVisibleFlgTrue(@Param("id") Long id);

	// 有効かつクラシックではない賛美歌をID昇順で全件取得
	// 対応Dao: HymnDao#selectByVisibleFlgTrueAndClassicalFalseOrderByIdAsc
	@Query("SELECT HM.ID, HM.NAME_JP, HM.NAME_KR, HM.LINK, HM.UPDATED_TIME, HM.UPDATED_USER, HM.LYRIC, HM.VISIBLE_FLG, HM.CLASSICAL"
			+ " FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.CLASSICAL = 'false' ORDER BY HM.ID ASC")
	List<Hymn> findByVisibleFlgTrueAndClassicalFalseOrderByIdAsc();

	// 有効な賛美歌をID昇順で全件取得
	// 対応Dao: HymnDao#selectByVisibleFlgTrueOrderByIdAsc
	@Query("SELECT HM.ID, HM.NAME_JP, HM.NAME_KR, HM.LINK, HM.UPDATED_TIME, HM.UPDATED_USER, HM.LYRIC, HM.VISIBLE_FLG, HM.CLASSICAL"
			+ " FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' ORDER BY HM.ID ASC")
	List<Hymn> findByVisibleFlgTrueOrderByIdAsc();

	// 有効な賛美歌の最新更新時間を取得
	// 対応Dao: HymnDao#selectMaxUpdatedTime
	@Query("SELECT MAX(HM.UPDATED_TIME) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true'")
	LocalDateTime findMaxUpdatedTime();

	// 賛美歌を論理削除（VISIBLE_FLG = 'false'）
	// 対応Dao: HymnDao#deleteLogically
	// ※INSERT/UPDATEはSpring Data JDBCのsave()で対応
	@Modifying
	@Query("UPDATE HYMNS SET VISIBLE_FLG = 'false' WHERE ID = :id")
	void deleteLogically(@Param("id") Long id);
}
