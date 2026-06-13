package app.preach.gospel.model;

/**
 * 役割テーブル
 *
 * <p>MyBatis移行により以下を変更:</p>
 * <ul>
 *   <li>Spring Data JDBC固有アノテーション(@Table/@Column/@MappedCollection)を除去</li>
 *   <li>Set&lt;AuthorityRef&gt;を除去 → ROLE_AUTHへのアクセスはRoleMapperで管理</li>
 *   <li>withAddedAuthority()を除去 → 権限追加処理はService層でMapperを呼び出す形に移行</li>
 * </ul>
 *
 * @author ArkamaHozota
 */
public record Role(Long id, String name, String visibleFlg) {
}
