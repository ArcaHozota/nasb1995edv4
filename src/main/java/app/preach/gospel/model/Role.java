package app.preach.gospel.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 役割テーブル
 *
 * @author ArkamaHozota
 */
@Table("ROLES")
public record Role(@Id @Column("ID") Long id, @Column("NAME") String name, @Column("VISIBLE_FLG") String visibleFlg,
		// Spring Data JDBCが自動的に中間テーブル(ROLE_AUTH)と紐付けます
		// ROLE_AUTHテーブル側の外部キーカラム名を明示的に指定する
		@MappedCollection(idColumn = "ROLE_ID") Set<AuthorityRef> authorities, @Transient Boolean isNewEntity)
		implements Persistable<Long> {

	/**
	 * 新規作成用のコンストラクタ。isNewEntity を true に固定する。
	 */
	public Role(final Long id, final String name, final String visibleFlg, final Set<AuthorityRef> authorities) {
		this(id, name, visibleFlg, authorities, true);
	}

	/**
	 * イミュータブルなrecord型で、権限を追加するための便利メソッド（Witherパターン） isNewEntity は元のインスタンスの状態を引き継ぐ。
	 */
	public Role withAddedAuthority(final Long authId) {
		final var newAuths = new HashSet<AuthorityRef>(this.authorities);
		newAuths.add(new AuthorityRef(authId));
		return new Role(this.id, this.name, this.visibleFlg, java.util.Collections.unmodifiableSet(newAuths),
				this.isNewEntity);
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