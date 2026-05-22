package app.preach.gospel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 節別情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "節別情報転送クラス")
public record VerseDto(@Schema(description = "ID", example = "33", requiredMode = RequiredMode.REQUIRED) Long id,
		@Schema(description = "英語名称", example = "Luke 21:33", requiredMode = RequiredMode.REQUIRED) String name,
		@Schema(description = "英語内容", example = "Heaven and earth will pass away, but My words will by no means pass away.", requiredMode = RequiredMode.REQUIRED) String textEn,
		@Schema(description = "日本語内容", example = "天地は滅びるが、わたしの言葉は決して滅びない。", requiredMode = RequiredMode.REQUIRED) String textJp,
		@Schema(description = "章節ID", example = "33", requiredMode = RequiredMode.REQUIRED) String chapterId) {
}
