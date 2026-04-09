package edu.eci.dosw.tdd.core.Validator;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario debe ser diferente de null");
        }
        if (ValidationUtil.isNullOrEmpty(user.getName())) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacio.");
        }
        if (ValidationUtil.isNullOrEmpty(user.getUsername())) {
            throw new IllegalArgumentException("El username no puede estar vacio.");
        }
        if (ValidationUtil.isNullOrEmpty(user.getPassword())) {
            throw new IllegalArgumentException("La contrasena no puede estar vacia.");
        }
        if (user.getRole() != null &&
                !user.getRole().equals("USER") &&
                !user.getRole().equals("LIBRARIAN")) {
            throw new IllegalArgumentException("El rol debe ser USER o LIBRARIAN.");
        }
    }
}