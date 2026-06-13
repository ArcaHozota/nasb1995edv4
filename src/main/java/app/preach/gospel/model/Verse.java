package app.preach.gospel.model;

/**
 * 聖書節別テーブル
 *
 * @author ArkamaHozota
 */
public record Verse(Long id, String name, String textEn, String textJp, Integer chapterId, String changeLine) {
}
