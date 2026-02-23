package com.pence.service;

import com.pence.model.Pet;
import com.pence.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Service
public class NotificationService {

    private static final Logger log = Logger.getLogger(NotificationService.class.getName());

    private final PetRepository petRepository;
    private final ExecutorService executor;

    public NotificationService(PetRepository petRepository) {
        this.petRepository = petRepository;
        this.executor      = Executors.newFixedThreadPool(4);
    }

    // Yeni heyvan əlavə olunanda — yaxın istifadəçilərə bildiriş göndər
    public void notifyNearbyUsers(Pet newPet) {
        executor.submit(() -> {
            if (newPet.getLatitude() == null || newPet.getLongitude() == null) return;

            double radius = 0.018; // ~2 km

            List<Pet> nearby = petRepository.findByLatitudeBetweenAndLongitudeBetween(
                    newPet.getLatitude()  - radius, newPet.getLatitude()  + radius,
                    newPet.getLongitude() - radius, newPet.getLongitude() + radius
            );

            log.info("Bildiriş göndəriləcək: " + nearby.size() + " istifadəçi");

            for (Pet p : nearby) {
                // Real sistemdə: SMS API
                log.info("Bildiriş → " + p.getOwnerPhone());
            }
        });
    }

    // Consumer<T> — bildiriş göndərmə strategiyasını xaricdən ver
    public void sendNotification(String contact, Consumer<String> sender) {
        executor.submit(() -> {
            try {
                sender.accept(contact);
                log.info("Göndərildi: " + contact);
            } catch (Exception e) {
                log.warning("Göndərilmədi: " + e.getMessage());
            }
        });
    }

    // Future — nəticəni gözlə
    public Future<Boolean> sendWithConfirmation(String phone) {
        Callable<Boolean> task = () -> {
            try {
                Thread.sleep(500); // SMS simulyasiyası
                log.info("SMS göndərildi: " + phone);
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        };
        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
