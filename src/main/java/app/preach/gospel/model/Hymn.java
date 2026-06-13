package app.preach.gospel.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 賛美歌テーブル
 *
 * @author ArkamaHozota
 */
@Table("HYMNS")
public record Hymn(@Id @Column("ID") Long id, @Column("NAME_JP") String nameJp, @Column("NAME_KR") String nameKr,
		@Column("LINK") String link, @Column("UPDATED_TIME") LocalDateTime updatedTime,
		@Column("UPDATED_USER") Long updatedUser, @Column("LYRIC") String lyric,
		@Column("VISIBLE_FLG") String visibleFlg, @Column("CLASSICAL") String classical, @Transient Boolean isNewEntity)
		implements Persistable<Long> {

	/**
	 * 新規作成用のコンストラクタ。isNewEntity を true に固定する。
	 */
	public Hymn(final Long id, final String nameJp, final String nameKr, final String link,
			final LocalDateTime updatedTime, final Long updatedUser, final String lyric, final String visibleFlg,
			final String classical) {
		this(id, nameJp, nameKr, link, updatedTime, updatedUser, lyric, visibleFlg, classical, true);
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isNew() {
		// isNewEntity が null（DBからの読み込み時）の場合は false とみなす
		return Boolean.TRUE.equals(this.isNewEntity);
	}

}