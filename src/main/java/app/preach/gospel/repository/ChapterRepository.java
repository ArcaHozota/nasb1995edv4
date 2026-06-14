package app.preach.gospel.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Chapter;

/**
 * 聖書章節リポジトリ (Spring Data JDBC)
 *
 * @author ArkamaHozota
 */
@Repository
public interface ChapterRepository extends ListCrudRepository<Chapter, Integer> {

	// bookId指定、かつID昇順で章節を取得
	// 対応Dao: ChapterDao#selectByBookIdOrderByIdAsc
	List<Chapter> findByBookIdOrderByIdAsc(Integer bookId);
}
