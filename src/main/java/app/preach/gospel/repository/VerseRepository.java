package app.preach.gospel.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
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

	@Modifying
	@Query("INSERT INTO VERSES (ID, NAME, TEXT_EN, TEXT_JP, CHAPTER_ID, CHANGE_LINE) "
			+ "VALUES (:id, :name, :textEn, :textJp, :chapterId, :changeLine)")
	void insertOne(@Param("id") Long id, @Param("name") String name, @Param("textEn") String textEn,
			@Param("textJp") String textJp, @Param("chapterId") Integer chapterId,
			@Param("changeLine") String changeLine);
}
