package app.preach.gospel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 賛美歌楽譜テーブル
 *
 * @author ArkamaHozota
 */
@Table("HYMNS_WORK")
public record HymnWork(@Id @Column("ID") Long id, @Column("WORK_ID") Long workId, @Column("SCORE") byte[] score,
		@Transient Boolean isNewEntity) implements Persistable<Long> {

	public HymnWork(final Long id, final Long workId, final byte[] score) {
		this(id, workId, score, true);
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