package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (ValidationUtil.isNullOrEmpty(user.getName())) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
    }
}