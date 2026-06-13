package app.preach.gospel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 聖書書別テーブル
 *
 * @author ArkamaHozota
 */
@Table("BOOKS")
public record Book(@Id @Column("ID") Short id, @Column("NAME") String name, @Column("NAME_JP") String nameJp) {
}
