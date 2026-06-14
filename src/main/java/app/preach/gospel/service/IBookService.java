package app.preach.gospel.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import app.preach.gospel.pojo.BookDto;
import app.preach.gospel.pojo.ChapterDto;
import app.preach.gospel.pojo.VerseDto;
import app.preach.gospel.utils.CoResult;

/**
 * 聖書章節サービスインターフェス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface IBookService {

	/**
	 * 聖書書別情報を取得する
	 *
	 * @return CoResult<List<BookDto>, DataAccessException>
	 */
	CoResult<List<BookDto>, DataAccessException> getBooks();

	/**
	 * 聖書章節情報を取得する
	 *
	 * @param id 書別ID
	 * @return CoResult<List<ChapterDto>, DataAccessException>
	 */
	CoResult<List<ChapterDto>, DataAccessException> getChaptersByBookId(Short id);

	/**
	 * 聖書節別情報を保存する
	 *
	 * @param phraseDto 節別情報転送クラス
	 * @return CoResult<String, DataAccessException>
	 */
	CoResult<String, DataAccessException> infoStorage(VerseDto phraseDto);
}
