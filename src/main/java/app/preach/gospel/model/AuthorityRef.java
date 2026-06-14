package app.preach.gospel.model;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * ROLE_AUTH 中間テーブルを表す埋め込み用レコード
 */
@Table("ROLE_AUTH")
public record AuthorityRef(@Column("AUTH_ID") Long authId) {
}
