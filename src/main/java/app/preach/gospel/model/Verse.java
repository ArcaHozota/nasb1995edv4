package app.preach.gospel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
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
		@Column("CHANGE_LINE") String changeLine, @Transient Boolean isNewEntity) implements Persistable<Long> {

	/**
	 * 新規作成用のコンストラクタ。isNewEntity を true に固定する。
	 */
	public Verse(final Long id, final String name, final String textEn, final String textJp, final Integer chapterId,
			final String changeLine) {
		this(id, name, textEn, textJp, chapterId, changeLine, true);
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		return Boolean.TRUE.equals(this.isNewEntity);
	}

}