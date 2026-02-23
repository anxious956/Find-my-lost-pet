package com.pence.exception;

public class PetNotFoundException extends RuntimeException {

    private final Long petId;

    public PetNotFoundException(String message) {
        super(message);
        this.petId = null;
    }

    public PetNotFoundException(Long petId) {
        super("Heyvan tapılmadı: ID=" + petId);
        this.petId = petId;
    }

    public Long getPetId() {
        return petId;
    }
}
