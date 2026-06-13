package app.preach.gospel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 聖書章節テーブル
 *
 * @author ArkamaHozota
 */
@Table("CHAPTERS")
public record Chapter(@Id @Column("ID") Integer id, @Column("NAME") String name, @Column("NAME_JP") String nameJp,
		@Column("BOOK_ID") Integer bookId) {
}
