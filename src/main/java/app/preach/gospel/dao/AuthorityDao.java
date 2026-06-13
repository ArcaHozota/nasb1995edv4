package app.preach.gospel.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import app.preach.gospel.model.Authority;

/**
 * 権限Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface AuthorityDao {

	/**
	 * 全権限を取得
	 */
	List<Authority> selectAll();

	/**
	 * IDリストで権限を取得
	 */
	List<Authority> selectByIds(List<Long> ids);

	/**
	 * IDで1件取得
	 */
	Optional<Authority> selectById(Long id);
}
