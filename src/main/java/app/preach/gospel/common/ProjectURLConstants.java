package app.preach.gospel.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * プロジェクトURLコンスタント
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectURLConstants {

	public static final String URL_BOOKS_NAMESPACE = "/books";

	public static final String URL_CATEGORY_NAMESPACE = "/category";

	public static final String URL_CHECK_DELETE = "deletion-check";

	public static final String URL_CHECK_EDITION = "edition-check";

	public static final String URL_CHECK_NAME = "check-duplicated";

	public static final String URL_CHECK_NAME2 = "check-duplicated2";

	public static final String URL_GET_CHAPTERS = "get-chapters";

	public static final String URL_GET_INFO_ID = "get-info-id";

	public static final String URL_HYMNS_NAMESPACE = "/hymns";

	public static final String URL_INFO_DELETION = "info-deletion";

	public static final String URL_INFO_STORAGE = "info-storage";

	public static final String URL_INFO_UPDATE = "info-update";

	public static final String URL_LOGIN = "do-login";

	public static final String URL_LOGOUT = "do-logout";

	public static final String URL_PAGINATION = "pagination";

	public static final String URL_PRE_LOGIN = "pre-login";

	public static final String URL_RANDOM_RETRIEVE = "random-retrieve";

	public static final String URL_REGISTER = "toroku";

	public static final String URL_SCORE_DOWNLOAD = "score-download";

	public static final String URL_SCORE_UPLOAD = "score-upload";

	public static final String URL_STATIC_RESOURCE = "/static/**";

	public static final String URL_STUDENTS_NAMESPACE = "/students";

	public static final String URL_TO_ADDITION = "to-addition";

	public static final String URL_TO_EDITION = "to-edition";

	public static final String URL_TO_ERROR = "to-system-error";

	public static final String URL_TO_LOGIN = "login";

	public static final String URL_TO_LOGIN_WITH_ERROR = "login-with-error";

	public static final String URL_TO_MAINMENU = "to-mainmenu";

	public static final String URL_TO_MAINMENU_WITH_LOGIN = "to-mainmenu-with-login";

	public static final String URL_TO_PAGES = "to-pages";

	public static final String URL_TO_RANDOM_FIVE = "to-random-five";

	public static final String URL_TO_SCORE_UPLOAD = "to-score-upload";
}
