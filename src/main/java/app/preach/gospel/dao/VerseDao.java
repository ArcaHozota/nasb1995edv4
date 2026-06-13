package app.preach.gospel.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import app.preach.gospel.model.Verse;

/**
 * 聖書節Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface VerseDao {

	/**
	 * 聖書節を1件登録
	 */
	int insert(Verse verse);

	/**
	 * 全節を取得
	 */
	List<Verse> selectAll();

	/**
	 * chapterId指定でID昇順に取得
	 */
	List<Verse> selectByChapterIdOrderByIdAsc(Integer chapterId);

	/**
	 * IDで1件取得
	 */
	Optional<Verse> selectById(Long id);

	/**
	 * 聖書節を1件更新
	 */
	int update(Verse verse);
}
