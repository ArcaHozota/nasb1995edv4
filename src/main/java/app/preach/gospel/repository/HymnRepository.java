package app.preach.gospel.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Hymn;

/**
 * 賛美歌リポ
 *
 * @author ArkamaHozota
 */
@Repository
public interface HymnRepository extends ListCrudRepository<Hymn, Long> {

	// 1. 全件数取得
	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true'")
	long countByVisibleFlgTrue();

	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_JP = :nameJp")
	int countByVisibleFlgTrueAndNameJp(String nameJp);

	// 重複チェック用
	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_JP = :nameJp AND HM.ID <> :id")
	int countByVisibleFlgTrueAndNameJpAndIdNot(@Param("nameJp") String nameJp, @Param("id") Long id);

	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_KR = :nameKr")
	int countByVisibleFlgTrueAndNameKr(@Param("nameKr") String nameKr);

	@Query("SELECT COUNT(1) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_KR = :nameKr AND HM.ID <> :id")
	int countByVisibleFlgTrueAndNameKrAndIdNot(@Param("nameKr") String nameKr, @Param("id") Long id);

	// 5. NAME_KRに対するLIKE検索
	@Query("SELECT * FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.NAME_KR LIKE :keyword ORDER BY HM.ID ASC")
	List<Hymn> findActiveHymnsByNameKrLike(@Param("keyword") String keyword);

	// 有効な賛美歌をIDで1件取得
	@Query("SELECT * FROM HYMNS HM WHERE HM.ID = :id AND HM.VISIBLE_FLG = 'true'")
	Optional<Hymn> findByIdAndVisibleFlgTrue(@Param("id") Long id);

	// 4. 有効かつクラシックではない(CLASSICAL = 0)賛美歌をID昇順で全件取得
	@Query("SELECT * FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' AND HM.CLASSICAL = 'false' ORDER BY HM.ID ASC")
	List<Hymn> findByVisibleFlgTrueAndClassicalFalseOrderByIdAsc();

	// 3. 有効な賛美歌をID昇順で全件取得
	@Query("SELECT * FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true' ORDER BY HM.ID ASC")
	List<Hymn> findByVisibleFlgTrueOrderByIdAsc();

	// 2. 最新の更新時間を取得する
	@Query("SELECT MAX(HM.UPDATED_TIME) FROM HYMNS HM WHERE HM.VISIBLE_FLG = 'true'")
	LocalDateTime findMaxUpdatedTime();
}