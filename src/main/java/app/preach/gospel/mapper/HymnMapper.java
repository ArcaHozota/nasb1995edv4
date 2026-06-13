package app.preach.gospel.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.model.Hymn;
import app.preach.gospel.model.HymnWork;
import app.preach.gospel.model.Student;
import app.preach.gospel.utils.CoStringUtils;
import app.preach.gospel.utils.LineNumber;

/**
 * エンティティ2DTOマッパー
 *
 * @author aArkamahozota
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HymnMapper {

	@Named("formatLocalDateTime")
	default String formatLocalDateTime(final LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(localDateTime);
	}

	// ★マーク付与ロジック
	@Named("mapNameJp")
	default String mapNameJp(final Hymn hymn) {
		if (hymn == null) {
			return null;
		}
		return CoStringUtils.isEqual(hymn.classical(), Boolean.TRUE.toString()) ? "★" + hymn.nameJp() : hymn.nameJp();
	}

	@Named("objectToString")
	default String objectToString(final Object obj) {
		return obj != null ? obj.toString() : null;
	}

	/**
	 * Hymn, HymnWork, Student エンティティを集約して HymnDto へマッピングする
	 */
	@Mapping(target = "id", source = "hymn.id")
	@Mapping(target = "nameJp", source = "hymn.nameJp")
	@Mapping(target = "nameKr", source = "hymn.nameKr")
	@Mapping(target = "lyric", source = "hymn.lyric")
	@Mapping(target = "link", source = "hymn.link")
	@Mapping(target = "score", source = "work.score")
	@Mapping(target = "updatedUser", source = "student.username") // DTOに準拠し、ユーザー名を設定
	@Mapping(target = "updatedTime", source = "formattedTime") // 引数で受け取ったフォーマット済みの時間文字列を設定
	@Mapping(target = "lineNumber", ignore = true) // 単体取得時はnull初期化のためignore
	HymnDto toDto(Hymn hymn, HymnWork work, Student student, String formattedTime);

	/**
	 * Hymn エンティティから 基本的な HymnDto へのマッピング定義2
	 */
	// 引数が複数あるため、hymnオブジェクトの中身は "hymn.xxx" として指定します
	@Mapping(target = "nameJp", source = "hymn", qualifiedByName = "mapNameJp")
	@Mapping(target = "nameKr", source = "hymn.nameKr")
	@Mapping(target = "lyric", source = "hymn.lyric")
	@Mapping(target = "link", source = "hymn.link")
	@Mapping(target = "updatedUser", source = "hymn.updatedUser", qualifiedByName = "objectToString")
	@Mapping(target = "updatedTime", source = "hymn.updatedTime", qualifiedByName = "objectToString")
	@Mapping(target = "lineNumber", source = "lineNumber") // こちらは引数の lineNumber をそのままマッピング
	@Mapping(target = "score", ignore = true)
	HymnDto toDto2(Hymn hymn, LineNumber lineNumber);
}