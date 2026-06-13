package app.preach.gospel.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import app.preach.gospel.model.Hymn;

/**
 * 賛美歌Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface HymnDao {

	/**
	 * 有効な賛美歌の全件数を取得
	 */
	long countByVisibleFlgTrue();

	/**
	 * 有効かつ名前(JP)に一致する件数を取得
	 */
	int countByVisibleFlgTrueAndNameJp(String nameJp);

	/**
	 * 有効かつ名前(JP)に一致し、指定IDを除く件数を取得（重複チェック用）
	 */
	int countByVisibleFlgTrueAndNameJpAndIdNot(@Param("nameJp") String nameJp, @Param("id") Long id);

	/**
	 * 有効かつ名前(KR)に一致する件数を取得
	 */
	int countByVisibleFlgTrueAndNameKr(String nameKr);

	/**
	 * 有効かつ名前(KR)に一致し、指定IDを除く件数を取得（重複チェック用）
	 */
	int countByVisibleFlgTrueAndNameKrAndIdNot(@Param("nameKr") String nameKr, @Param("id") Long id);

	/**
	 * 有効な賛美歌をNAME_KRでLIKE検索、ID昇順
	 */
	List<Hymn> selectActiveByNameKrLike(String keyword);

	/**
	 * IDで有効な賛美歌を1件取得
	 */
	Optional<Hymn> selectByIdAndVisibleFlgTrue(Long id);

	/**
	 * 有効かつクラシックではない賛美歌をID昇順で全件取得
	 */
	List<Hymn> selectByVisibleFlgTrueAndClassicalFalseOrderByIdAsc();

	/**
	 * 有効な賛美歌をID昇順で全件取得
	 */
	List<Hymn> selectByVisibleFlgTrueOrderByIdAsc();

	/**
	 * 有効な賛美歌の最新更新時間を取得
	 */
	LocalDateTime selectMaxUpdatedTime();

	/**
	 * 賛美歌を1件登録
	 */
	int insert(Hymn hymn);

	/**
	 * 賛美歌を1件更新
	 */
	int update(Hymn hymn);

	/**
	 * 賛美歌を論理削除（VISIBLE_FLG = 'false'）
	 */
	int deleteLogically(Long id);
}
