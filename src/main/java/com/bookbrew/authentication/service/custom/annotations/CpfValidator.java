package com.bookbrew.authentication.service.custom.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

    @Override
    public void initialize(Cpf constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {

        if (cpf == null || cpf.isEmpty()) {
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        return isValidCpf(cpf);
    }

    private boolean isValidCpf(String cpf) {
        try {
            int sum = 0, weight = 10;

            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * weight--;
            }

            int firstCheckDigit = 11 - (sum % 11);

            if (firstCheckDigit > 9)
                firstCheckDigit = 0;

            sum = 0;
            weight = 11;

            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * weight--;
            }

            int secondCheckDigit = 11 - (sum % 11);

            if (secondCheckDigit > 9)
                secondCheckDigit = 0;

            return cpf.endsWith("" + firstCheckDigit + secondCheckDigit);

        } catch (Exception e) {
            return false;
        }
    }
}
