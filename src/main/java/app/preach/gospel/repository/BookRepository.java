package app.preach.gospel.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Book;

/**
 * 聖書書別リポ
 *
 * @author ArkamaHozota
 */
@Repository
public interface BookRepository extends ListCrudRepository<Book, Short> {
	// ID昇順で全書別を取得
	List<Book> findAllByOrderByIdAsc();
}
