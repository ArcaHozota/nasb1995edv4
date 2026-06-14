package app.preach.gospel.pojo;

import app.preach.gospel.utils.LineNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 賛美情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "賛美情報転送クラス")
public record HymnDto(
		@Schema(description = "ID", example = "0123456789876543210", requiredMode = RequiredMode.REQUIRED) Long id,
		@Schema(description = "日本語名称", example = "主を讃えよ") String nameJp,
		@Schema(description = "韓国語名称", example = "비 준비하시니") String nameKr,
		@Schema(description = "歌詞", example = "0123456789876543210") String lyric,
		@Schema(description = "ビデオリンク", example = "https://youtu.be/123456") String link,
		@Schema(description = "楽譜") byte[] score,
		@Schema(description = "更新者", example = "0123456789876543210") String updatedUser,
		@Schema(description = "更新時間", example = "2005-11-18 11:22:23") String updatedTime,
		@Schema(description = "LINENUMBER") LineNumber lineNumber) {
}
