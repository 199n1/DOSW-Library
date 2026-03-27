package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoanPersistenceMapper {

    private final UserPersistenceMapper userMapper;
    private final BookPersistenceMapper bookMapper;

    public Loan toDomain(LoanEntity entity) {
        return Loan.builder()
                .id(entity.getId())
                .user(userMapper.toDomain(entity.getUser()))
                .book(bookMapper.toDomain(entity.getBook()))
                .loanDate(entity.getLoanDate())
                .returnDate(entity.getReturnDate())
                .status(LoanStatus.valueOf(entity.getStatus().name()))
                .build();
    }

    public LoanEntity toEntity(Loan loan,
                               edu.eci.dosw.tdd.persistence.entity.UserEntity userEntity,
                               edu.eci.dosw.tdd.persistence.entity.BookEntity bookEntity) {
        return LoanEntity.builder()
                .id(loan.getId())
                .user(userEntity)
                .book(bookEntity)
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .status(LoanEntity.LoanStatus.valueOf(loan.getStatus().name()))
                .build();
    }
}