package app.preach.gospel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 聖書節別テーブル
 *
 * @author ArkamaHozota
 */
@Table("VERSES")
public record Verse(@Id @Column("ID") Long id, @Column("NAME") String name, @Column("TEXT_EN") String textEn,
		@Column("TEXT_JP") String textJp, @Column("CHAPTER_ID") Integer chapterId,
		@Column("CHANGE_LINE") Boolean changeLine) {
}
