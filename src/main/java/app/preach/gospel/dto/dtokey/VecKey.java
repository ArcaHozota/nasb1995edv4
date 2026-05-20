package app.preach.gospel.dto.dtokey;

/**
 * ベクターキー
 */
public record VecKey(String lang, String hymnalVersion, long hymnId, String textHash) {
}
