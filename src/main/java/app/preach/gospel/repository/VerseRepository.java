package app.preach.gospel.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import app.preach.gospel.model.Verse;

/**
 * 聖書節リポ
 *
 * @author ArkamaHozota
 */
@Repository
public interface VerseRepository extends ListCrudRepository<Verse, Long> {
}
