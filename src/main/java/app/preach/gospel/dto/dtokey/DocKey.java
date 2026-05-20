package app.preach.gospel.dto.dtokey;

import java.io.Serializable;

/**
 * Docキー
 */
public record DocKey(String keyword, String corpusVersion, Long suryo) implements Serializable {
}
