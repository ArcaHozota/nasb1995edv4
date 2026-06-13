package app.preach.gospel.model;

import java.time.LocalDateTime;

/**
 * 賛美歌テーブル
 *
 * @author ArkamaHozota
 */
public record Hymn(Long id, String nameJp, String nameKr, String link, LocalDateTime updatedTime, Long updatedUser,
		String lyric, String visibleFlg, String classical) {
}
