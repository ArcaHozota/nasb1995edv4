package app.preach.gospel.dto.dtokey;

import java.io.Serializable;

/**
 * トークンキー
 */
public record TokKey(String lang, String tokenizer, String textHash) implements Serializable {
}
