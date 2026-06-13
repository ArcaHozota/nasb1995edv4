package app.preach.gospel.model;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 賛美歌テーブル
 *
 * @author ArkamaHozota
 */
@Table("HYMNS")
public record Hymn(@Id @Column("ID") Long id, @Column("NAME_JP") String nameJp, @Column("NAME_KR") String nameKr,
		@Column("LINK") String link, @Column("UPDATED_TIME") OffsetDateTime updatedTime,
		@Column("UPDATED_USER") Long updatedUser, @Column("LYRIC") String lyric,
		@Column("VISIBLE_FLG") Boolean visibleFlg, @Column("CLASSICAL") Boolean classical) {
}
