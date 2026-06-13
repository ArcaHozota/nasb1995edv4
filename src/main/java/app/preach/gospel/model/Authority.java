package app.preach.gospel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 権限テーブル
 *
 * @author ArkamaHozota
 */
@Table("AUTHORITIES")
public record Authority(@Id @Column("ID") Long id, @Column("NAME") String name, @Column("TITLE") String title,
		@Column("CATEGORY_ID") Long categoryId) {
}
