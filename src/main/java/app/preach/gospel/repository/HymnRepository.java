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

	// 1. 全件数取得 (VISIBLE_FLG = 1)
	long countByVisibleFlgTrue();

	int countByVisibleFlgTrueAndNameJp(String nameJp);

	// 重複チェック用（IDを指定する場合としない場合）
	int countByVisibleFlgTrueAndNameJpAndIdNot(String nameJp, Long id);

	int countByVisibleFlgTrueAndNameKr(String nameKr);

	int countByVisibleFlgTrueAndNameKrAndIdNot(String nameKr, Long id);

	// 5. NAME_KRに対するLIKE検索（大文字小文字の差異を吸収するためOracle標準に合わせて記述）
	@Query("SELECT * FROM HYMNS WHERE VISIBLE_FLG = 1 AND NAME_KR LIKE :keyword ORDER BY ID ASC")
	List<Hymn> findActiveHymnsByNameKrLike(@Param("keyword") String keyword);

	// 有効な賛美歌をIDで1件取得
	Optional<Hymn> findByIdAndVisibleFlgTrue(Long id);

	// 4. 有効かつクラシックではない(CLASSICAL = 0)賛美歌をID昇順で全件取得
	List<Hymn> findByVisibleFlgTrueAndClassicalFalseOrderByIdAsc();

	// 3. 有効な賛美歌をID昇順で全件取得
	List<Hymn> findByVisibleFlgTrueOrderByIdAsc();

	// 2. 最新の更新時間を取得する（MAX(updated_time)）
	@Query("SELECT MAX(UPDATED_TIME) FROM HYMNS")
	LocalDateTime findMaxUpdatedTime();
}
