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
	 * 全節を取得
	 */
	List<Verse> selectAll();

	/**
	 * IDで1件取得
	 */
	Optional<Verse> selectById(Long id);

	/**
	 * chapterId指定でID昇順に取得
	 */
	List<Verse> selectByChapterIdOrderByIdAsc(Integer chapterId);
}
