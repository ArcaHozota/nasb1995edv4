package app.preach.gospel.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import app.preach.gospel.model.Chapter;

/**
 * 聖書章節Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface ChapterDao {

	/**
	 * bookId指定、かつID昇順で章節を取得
	 */
	List<Chapter> selectByBookIdOrderByIdAsc(Integer bookId);

	/**
	 * IDで1件取得
	 */
	Optional<Chapter> selectById(Integer id);
}
