package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    List<LoanEntity> findByUser(UserEntity user);

    long countByUserIdAndStatus(String userId, LoanEntity.LoanStatus status);

    Optional<LoanEntity> findByUserIdAndBookIdAndStatus(
            String userId,
            String bookId,
            LoanEntity.LoanStatus status
    );
}