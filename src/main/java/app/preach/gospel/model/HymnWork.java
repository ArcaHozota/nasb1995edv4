package app.preach.gospel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 賛美歌楽譜テーブル
 *
 * @author ArkamaHozota
 */
@Table("HYMNS_WORK")
public record HymnWork(@Id @Column("ID") Long id, @Column("WORK_ID") Long workId, @Column("SCORE") byte[] score) {
}
