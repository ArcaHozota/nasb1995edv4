package app.preach.gospel.controller;

import java.io.Serial;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.VerseDto;
import app.preach.gospel.service.IBookService;
import app.preach.gospel.utils.CoResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

/**
 * 聖書章節入力ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RequestMapping("/books")
@Controller
@Tag(name = "聖書章節入力ハンドラ", description = "聖書章節入力に関わる操作を扱うエンドポイント")
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
	@GetMapping("/get-chapters")
	@ResponseBody
	@Operation(summary = "情報検索", description = "書目IDを指定した章節情報を取得する")
	public @NotNull ResponseEntity<List<ChapterDto>> getChapters(@RequestParam final Short bookId) {
		final CoResult<List<ChapterDto>, DataAccessException> chaptersByBookId = this.iBookService
				.getChaptersByBookId(bookId);
		if (!chaptersByBookId.isOk()) {
			throw chaptersByBookId.getErr();
		}
		return ResponseEntity.ok(chaptersByBookId.getData());
	}

	/**
	 * 聖書節別情報を保存する
	 *
	 * @param verseDto 情報転送クラス
	 * @return ResponseEntity<String>
	 */
	@PostMapping("/info-storage")
	@ResponseBody
	@Operation(summary = "情報保存", description = "聖書節別情報を保存する")
	public @NotNull ResponseEntity<String> infoStorage(@RequestBody final VerseDto verseDto) {
		final CoResult<String, DataAccessException> infoStorage = this.iBookService.infoStorage(verseDto);
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
	@GetMapping("/to-addition")
	@Operation(summary = "画面遷移", description = "情報追加画面へ移動する")
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
