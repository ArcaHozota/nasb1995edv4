package app.preach.gospel.dao;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import app.preach.gospel.model.HymnWork;

/**
 * 賛美歌楽譜Dao
 *
 * @author ArkamaHozota
 */
@Mapper
public interface HymnWorkDao {

	/**
	 * 楽譜テーブルの全レコード数カウント
	 */
	int countAllRecords();

	/**
	 * WORK_ID（HYMNSのID）を元に楽譜を1件取得
	 */
	Optional<HymnWork> selectByWorkId(Long workId);

	/**
	 * IDで1件取得
	 */
	Optional<HymnWork> selectById(Long id);

	/**
	 * 楽譜を1件登録
	 */
	int insert(HymnWork hymnWork);

	/**
	 * 楽譜を1件更新
	 */
	int update(HymnWork hymnWork);
}
