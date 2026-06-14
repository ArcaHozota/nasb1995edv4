package app.preach.gospel.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 奉仕者情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "奉仕者情報転送クラス")
public record StudentDto(
		@Schema(description = "ID", example = "0123456789876543210", requiredMode = RequiredMode.REQUIRED) Long id,
		@Schema(description = "アカウント", example = "Louis", requiredMode = RequiredMode.REQUIRED) String loginAccount,
		@Schema(description = "ユーザ名称", example = "ルイス14世") String username,
		@Schema(description = "パスワード", example = "123456") String password,
		@Schema(description = "メール", example = "louis@example.com") String email,
		@Schema(description = "生年月日", example = "2009/12/07") String dateOfBirth,
		@Schema(description = "役割ID", example = "0123456789876543210") Long roleId) {
}
