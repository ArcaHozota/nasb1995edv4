package app.preach.gospel.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 章節情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "章節情報転送クラス")
public record ChapterDto(@Schema(description = "ID", example = "15", requiredMode = RequiredMode.REQUIRED) Integer id,
		@Schema(description = "英語名称", example = "Psalms 33", requiredMode = RequiredMode.REQUIRED) String name,
		@Schema(description = "日本語名称", example = "詩編90編", requiredMode = RequiredMode.REQUIRED) String nameJp,
		@Schema(description = "書別ID", example = "33", requiredMode = RequiredMode.REQUIRED) String bookId) {
}
