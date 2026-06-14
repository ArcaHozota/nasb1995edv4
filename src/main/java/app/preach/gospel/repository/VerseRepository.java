package app.preach.gospel.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Verse;

/**
 * 聖書節リポジトリ (Spring Data JDBC)
 *
 * @author ArkamaHozota
 */
@Repository
public interface VerseRepository extends ListCrudRepository<Verse, Long> {

	// chapterId指定でID昇順に取得
	// 対応Dao: VerseDao#selectByChapterIdOrderByIdAsc
	List<Verse> findByChapterIdOrderByIdAsc(Integer chapterId);
}
