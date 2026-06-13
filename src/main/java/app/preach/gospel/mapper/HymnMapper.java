package app.preach.gospel.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.model.Hymn;
import app.preach.gospel.utils.LineNumber;

/**
 * エンティティ2DTOマッパー
 *
 * @author arkamahozota
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HymnMapper {

	// ★マーク付与ロジック
	@Named("mapNameJp")
	default String mapNameJp(final Hymn hymn) {
		if (hymn == null) {
			return null;
		}
		return Boolean.TRUE.equals(hymn.classical()) ? "★" + hymn.nameJp() : hymn.nameJp();
	}

	@Named("objectToString")
	default String objectToString(final Object obj) {
		return obj != null ? obj.toString() : null;
	}

	/**
	 * Hymn エンティティから 基本的な HymnDto へのマッピング定義
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
	@Mapping(target = "biko", ignore = true)
	HymnDto toDto(Hymn hymn, LineNumber lineNumber);
}