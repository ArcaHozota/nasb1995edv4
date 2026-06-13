package app.preach.gospel.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import app.preach.gospel.model.Book;

/**
 * 聖書書別Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface BookDao {

	/**
	 * ID昇順で全書別を取得
	 */
	List<Book> selectAllOrderByIdAsc();

	/**
	 * IDで1件取得
	 */
	Optional<Book> selectById(Short id);
}
