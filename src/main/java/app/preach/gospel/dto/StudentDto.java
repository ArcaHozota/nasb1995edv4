package app.preach.gospel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 奉仕者情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Schema(description = "奉仕者情報転送クラス")
public record StudentDto(

		/*
		 * ID
		 */
		Long id,

		/*
		 * アカウント
		 */
		String loginAccount,

		/*
		 * ユーザ名称
		 */
		String username,

		/*
		 * パスワード
		 */
		String password,

		/*
		 * メール
		 */
		String email,

		/*
		 * 生年月日
		 */
		String dateOfBirth,

		/*
		 * 役割ID
		 */
		String roleId) {
}
