package app.preach.gospel.dto;

import app.preach.gospel.utils.LineNumber;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 賛美情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "賛美情報転送クラス")
public record HymnDto(

		/*
		 * ID
		 */
		Long id,

		/*
		 * 日本語名称
		 */
		String nameJp,

		/*
		 * 韓国語名称
		 */
		String nameKr,

		/*
		 * 歌詞
		 */
		String lyric,

		/*
		 * ビデオリンク
		 */
		String link,

		/*
		 * 楽譜
		 */
		byte[] score,

		/*
		 * 備考
		 */
		String biko,

		/*
		 * 更新者
		 */
		String updatedUser,

		/*
		 * 更新時間
		 */
		String updatedTime,

		/*
		 * LINENUMBER
		 */
		LineNumber lineNumber) {
}
