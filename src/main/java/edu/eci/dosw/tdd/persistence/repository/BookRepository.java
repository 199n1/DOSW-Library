package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {
}