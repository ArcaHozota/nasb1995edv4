package app.preach.gospel.utils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 共通ページングクラス
 *
 * @param <T> データタイプ
 * @author ArkamaHozota
 * @since 1.00beta
 */
public final class Pagination<T> {

	/**
	 * 例外
	 */
	static class PaginationException extends RuntimeException {

		@Serial
		private static final long serialVersionUID = 4906724781325178457L;

		public PaginationException(final String message) {
			super(message);
		}
	}

	/**
	 * Paginationを取得する
	 *
	 * @param records      レコード
	 * @param totalRecords すべてのレコード数
	 * @param pageNum      当ページ
	 */
	@Contract("_, _, _ -> new")
	public static <T> @NotNull Pagination<T> of(final List<T> records, final long totalRecords, final int pageNum) {
		return new Pagination<>(records, totalRecords, pageNum, 12, 5);
	}

	/**
	 * Paginationを取得する
	 *
	 * @param records      レコード
	 * @param totalRecords すべてのレコード数
	 * @param pageNum      当ページ
	 * @param pageSize     ページサイズ
	 */
	@Contract("_, _, _, _ -> new")
	public static <T> @NotNull Pagination<T> of(final List<T> records, final long totalRecords, final int pageNum,
			final int pageSize) {
		return new Pagination<>(records, totalRecords, pageNum, pageSize, 5);
	}

	/**
	 * Paginationを取得する
	 *
	 * @param records       レコード
	 * @param totalRecords  すべてのレコード数
	 * @param pageNum       当ページ
	 * @param pageSize      ページサイズ
	 * @param navigatePages ナビゲーションのページ数
	 */
	@Contract("_, _, _, _, _ -> new")
	public static <T> @NotNull Pagination<T> of(final List<T> records, final long totalRecords, final int pageNum,
			final int pageSize, final int navigatePages) {
		return new Pagination<>(records, totalRecords, pageNum, pageSize, navigatePages);
	}

	/**
	 * 次のページはあるか
	 */
	private boolean hasNextPage = false;

	/**
	 * 前のページはあるか
	 */
	private boolean hasPrevPage = false;

	/**
	 * ナビゲーションの最初のページ
	 */
	private int naviFirstPage;

	/**
	 * ナビゲーションページの数の集合
	 */
	private int[] navigateNos;

	/**
	 * ナビゲーションのページ数
	 */
	private int navigatePages;

	/**
	 * ナビゲーションの最後のページ
	 */
	private int naviLastPage;

	/**
	 * 次のページ
	 */
	private int nextPage;

	/**
	 * 当ページ
	 */
	private int pageNum;

	/**
	 * ページサイズ
	 */
	private int pageSize;

	/**
	 * 前のページ
	 */
	private int prevPage;

	/**
	 * 毎ページのレコード
	 */
	private List<T> records;

	/**
	 * すべてのページ数
	 */
	private long totalPages;

	/**
	 * すべてのレコード数
	 */
	private long totalRecords;

	/**
	 * コンストラクタ
	 *
	 * @param records       レコード
	 * @param totalRecords  すべてのレコード数
	 * @param pageNum       当ページ
	 * @param pageSize      ページサイズ
	 * @param navigatePages ナビゲーションのページ数
	 */
	private Pagination(final List<T> records, final long totalRecords, final int pageNum, final int pageSize,
			final int navigatePages) {
		if (records != null && !records.isEmpty()) {
			this.pageNum = pageNum;
			this.records = records;
			this.pageSize = records.size();
			this.totalRecords = totalRecords;
			final long ape = this.totalRecords / pageSize;
			this.totalPages = this.totalRecords % pageSize == 0 ? ape : ape + 1;
		} else if (records != null) {
			this.pageNum = 1;
			this.records = new ArrayList<>();
			this.pageSize = 0;
			this.totalRecords = 0L;
			this.totalPages = 1L;
		} else {
			throw new PaginationException("データのコレクションは間違いました。");
		}
		this.calcByNavigatePages(navigatePages);
	}

	/**
	 * ナビゲーションのページ数によって色んな計算処理を行う
	 *
	 * @param navigatePages ナビゲーションのページ数
	 */
	private void calcByNavigatePages(final int navigatePages) {
		// ナビゲーションのページ数を設定する
		this.setNavigatePages(navigatePages);
		// ナビゲーションページの数の集合を取得する
		this.calcnavigateNums();
		// 前のページ、次のページ、最初及び最後のページを取得する
		this.calcPage();
		// ページングエッジを判断する
		this.discernPageBoundary();
	}

	/**
	 * ナビゲーションページの数の集合を取得する
	 */
	private void calcnavigateNums() {
		if (this.totalPages <= this.navigatePages) {
			this.navigateNos = new int[(int) this.totalPages];
			for (int i = 0; i < this.totalPages; i++) {
				this.navigateNos[i] = i + 1;
			}
			return;
		}
		this.navigateNos = new int[this.navigatePages];
		int startNum = this.pageNum - this.navigatePages / 2;
		int endNum = this.pageNum + this.navigatePages / 2;
		if (endNum > this.totalPages && startNum >= 1) {
			endNum = (int) this.totalPages;
			// 最後のナビゲーションページ
			for (int i = this.navigatePages - 1; i >= 0; i--) {
				this.navigateNos[i] = endNum;
				endNum--;
			}
		} else {
			if (startNum < 1) {
				startNum = 1;
			}
			// 他のナビゲーションページ
			for (int i = 0; i < this.navigatePages; i++) {
				this.navigateNos[i] = startNum;
				startNum++;
			}
		}
	}

	/**
	 * 前のページ、次のページ、最初及び最後のページを取得する
	 */
	private void calcPage() {
		if (this.navigateNos != null && this.navigateNos.length > 0) {
			this.naviFirstPage = this.navigateNos[0];
			this.naviLastPage = this.navigateNos[this.navigateNos.length - 1];
			if (this.pageNum > 1) {
				this.prevPage = this.pageNum - 1;
			}
			if (this.pageNum < this.totalPages) {
				this.nextPage = this.pageNum + 1;
			}
		}
	}

	/**
	 * ページングエッジを判断する
	 */
	private void discernPageBoundary() {
		this.hasPrevPage = this.pageNum > 1;
		this.hasNextPage = this.pageNum < this.totalPages;
	}

	public int getNaviFirstPage() {
		return this.naviFirstPage;
	}

	public int[] getNavigateNos() {
		return this.navigateNos;
	}

	public int getNavigatePages() {
		return this.navigatePages;
	}

	public int getNaviLastPage() {
		return this.naviLastPage;
	}

	public int getNextPage() {
		return this.nextPage;
	}

	public int getPageNum() {
		return this.pageNum;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getPrevPage() {
		return this.prevPage;
	}

	public List<T> getRecords() {
		return this.records;
	}

	public long getTotalPages() {
		return this.totalPages;
	}

	public long getTotalRecords() {
		return this.totalRecords;
	}

	/**
	 * 内容はあるかどうかを判断する
	 */
	public boolean hasContent() {
		return !this.records.isEmpty();
	}

	public boolean isHasNextPage() {
		return this.hasNextPage;
	}

	public boolean isHasPrevPage() {
		return this.hasPrevPage;
	}

	public void setHasNextPage(final boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public void setHasPrevPage(final boolean hasPrevPage) {
		this.hasPrevPage = hasPrevPage;
	}

	public void setNaviFirstPage(final int naviFirstPage) {
		this.naviFirstPage = naviFirstPage;
	}

	public void setNavigateNos(final int[] navigateNos) {
		this.navigateNos = navigateNos;
	}

	public void setNavigatePages(final int navigatePages) {
		this.navigatePages = navigatePages;
	}

	public void setNaviLastPage(final int naviLastPage) {
		this.naviLastPage = naviLastPage;
	}

	public void setNextPage(final int nextPage) {
		this.nextPage = nextPage;
	}

	public void setPageNum(final int pageNum) {
		this.pageNum = pageNum;
	}

	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPrevPage(final int prevPage) {
		this.prevPage = prevPage;
	}

	public void setRecords(final List<T> records) {
		this.records = records;
	}

	public void setTotalPages(final long totalPages) {
		this.totalPages = totalPages;
	}

	public void setTotalRecords(final long totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * toString
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "Pagination [records=" + this.records + ", pageNum=" + this.pageNum + ", pageSize=" + this.pageSize
				+ ", totalPages=" + this.totalPages + ", totalRecords=" + this.totalRecords + ", hasPrePage="
				+ this.hasPrevPage + ", hasNextPage=" + this.hasNextPage + ", prevPage=" + this.prevPage + ", nextPage="
				+ this.nextPage + ", navigatePages=" + this.navigatePages + ", naviFirstPage=" + this.naviFirstPage
				+ ", naviLastPage=" + this.naviLastPage + ", navigateNums=" + Arrays.toString(this.navigateNos) + "]";
	}

}