package app.preach.gospel.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 役割テーブル
 *
 * @author ArkamaHozota
 */
@Table("ROLES")
public record Role(@Id @Column("ID") Long id, @Column("NAME") String name, @Column("VISIBLE_FLG") String visibleFlg,
		// Spring Data JDBCが自動的に中間テーブル(ROLE_AUTH)と紐付けます
		// ※デフォルトでリレーションキーは「ROLE_ID」として扱われます
		Set<AuthorityRef> authorities) {
	/**
	 * イミュータブルなrecord型で、権限を追加するための便利メソッド（Witherパターン）
	 */
	public Role withAddedAuthority(final Long authId) {
		final var newAuths = new HashSet<AuthorityRef>(this.authorities);
		newAuths.add(new AuthorityRef(authId));
		return new Role(this.id, this.name, this.visibleFlg, java.util.Collections.unmodifiableSet(newAuths));
	}
}
