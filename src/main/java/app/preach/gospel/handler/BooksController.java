package app.preach.gospel.handler;

import java.io.Serial;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.PhraseDto;
import app.preach.gospel.service.IBookService;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;

/**
 * 聖書章節入力ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RequestMapping(ProjectURLConstants.URL_BOOKS_NAMESPACE)
@Controller
public final class BooksController {

	@Serial
	private static final long serialVersionUID = -6535194800678567557L;

	/**
	 * 聖書章節サービスインターフェス
	 */
	@Resource
	private IBookService iBookService;

	/**
	 * 章節情報を取得する
	 *
	 * @param bookId 聖書書別ID
	 * @return ResponseEntity<List<ChapterDto>>
	 */
	@GetMapping(ProjectURLConstants.URL_GET_CHAPTERS)
	@ResponseBody
	public @NotNull ResponseEntity<List<ChapterDto>> getChapters(@RequestParam final Short bookId) {
		final CoResult<List<ChapterDto>, DataAccessException> chaptersByBookId = this.iBookService
				.getChaptersByBookId(bookId);
		if (!chaptersByBookId.isOk()) {
			throw chaptersByBookId.getErr();
		}
		final List<ChapterDto> chapterDtos = chaptersByBookId.getData();
		return ResponseEntity.ok(chapterDtos);
	}

	/**
	 * 聖書節別情報を保存する
	 *
	 * @param phraseDto 情報転送クラス
	 * @return ResponseEntity<String>
	 */
	@PostMapping(ProjectURLConstants.URL_INFO_STORAGE)
	@ResponseBody
	public @NotNull ResponseEntity<String> infoStorage(@RequestBody final PhraseDto phraseDto) {
		final CoResult<String, DataAccessException> infoStorage = this.iBookService.infoStorage(phraseDto);
		if (!infoStorage.isOk()) {
			throw infoStorage.getErr();
		}
		return ResponseEntity.ok(infoStorage.getData());
	}

	/**
	 * 情報追加画面へ移動する
	 *
	 * @return ModelAndView
	 */
	@GetMapping(ProjectURLConstants.URL_TO_ADDITION)
	public @NotNull ModelAndView toAddition() {
		final CoResult<List<BookDto>, DataAccessException> books = this.iBookService.getBooks();
		final CoResult<List<ChapterDto>, DataAccessException> chaptersByBookId = this.iBookService
				.getChaptersByBookId(null);
		if (!books.isOk()) {
			throw books.getErr();
		}
		if (!chaptersByBookId.isOk()) {
			throw chaptersByBookId.getErr();
		}
		final ModelAndView modelAndView = new ModelAndView("books-addition");
		modelAndView.addObject("bookDtos", books.getData());
		modelAndView.addObject("chapterDtos", chaptersByBookId.getData());
		return modelAndView;
	}

}
