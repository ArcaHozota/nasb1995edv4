package app.preach.gospel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 章節情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "章節情報転送クラス")
public record ChapterDto(

		/*
		 * ID
		 */
		Integer id,

		/*
		 * 名称
		 */
		String name,

		/*
		 * 日本語名称
		 */
		String nameJp,

		/*
		 * 書別ID
		 */
		String bookId) {
}
