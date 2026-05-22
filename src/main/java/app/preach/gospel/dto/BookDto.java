package app.preach.gospel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 書別情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "書別情報転送クラス")
public record BookDto(@Schema(description = "ID", example = "33", requiredMode = RequiredMode.REQUIRED) Short id,
		@Schema(description = "英語名称", example = "Leviticus", requiredMode = RequiredMode.REQUIRED) String name,
		@Schema(description = "日本語名称", example = "ヨハネによる福音書", requiredMode = RequiredMode.REQUIRED) String nameJp) {
}
