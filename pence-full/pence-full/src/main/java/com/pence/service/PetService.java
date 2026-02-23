package com.pence.service;

import com.pence.dto.MatchResultDTO;
import com.pence.dto.PetDTO;
import com.pence.exception.PetNotFoundException;
import com.pence.model.Pet;
import com.pence.repository.PetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class PetService {

    private static final Logger log = Logger.getLogger(PetService.class.getName());

    private final PetRepository       petRepository;
    private final ImageHashService    imageHashService;
    private final NotificationService notificationService;
    private final ExecutorService     executor;

    @Value("${app.upload.lost-dir:uploads/lost}")
    private String lostDir;

    @Value("${app.upload.found-dir:uploads/found}")
    private String foundDir;

    @Value("${app.matching.threshold:15}")
    private int threshold;

    public PetService(PetRepository petRepository,
                      ImageHashService imageHashService,
                      NotificationService notificationService) {
        this.petRepository       = petRepository;
        this.imageHashService    = imageHashService;
        this.notificationService = notificationService;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    // ------------------------------------------------
    // İTMİŞ HEYVAN QEYDIYYATI
    // ------------------------------------------------

    public PetDTO registerLostPet(MultipartFile image,
                                   String ownerName, String ownerPhone,
                                   String petType, String description,
                                   String location, Double latitude, Double longitude,
                                   String baseUrl) throws IOException {
        validateImage(image);
        String imagePath = saveImage(image, lostDir);
        String imageHash;
        try (InputStream is = image.getInputStream()) {
            imageHash = imageHashService.calculateHash(is);
        }

        Pet pet = Pet.builder()
                .petType(Pet.PetType.valueOf(petType))
                .status(Pet.PetStatus.LOST)
                .ownerName(ownerName).ownerPhone(ownerPhone)
                .description(description).location(location)
                .latitude(latitude).longitude(longitude)
                .imagePath(imagePath).imageHash(imageHash)
                .build();

        Pet saved = petRepository.save(pet);
        notificationService.notifyNearbyUsers(saved);
        log.info("İtmiş heyvan qeydə alındı. ID=" + saved.getId());
        return PetDTO.fromEntity(saved, baseUrl);
    }

    // ------------------------------------------------
    // TAPILAN HEYVAN QEYDIYYATI
    // ------------------------------------------------

    public PetDTO registerFoundPet(MultipartFile image,
                                    String reporterName, String reporterPhone,
                                    String petType, String description,
                                    String location, Double latitude, Double longitude,
                                    String baseUrl) throws IOException {
        validateImage(image);
        String imagePath = saveImage(image, foundDir);
        String imageHash;
        try (InputStream is = image.getInputStream()) {
            imageHash = imageHashService.calculateHash(is);
        }

        Pet pet = Pet.builder()
                .petType(Pet.PetType.valueOf(petType))
                .status(Pet.PetStatus.FOUND)
                .ownerName(reporterName).ownerPhone(reporterPhone)
                .description(description).location(location)
                .latitude(latitude).longitude(longitude)
                .imagePath(imagePath).imageHash(imageHash)
                .build();

        Pet saved = petRepository.save(pet);
        notificationService.notifyNearbyUsers(saved);
        log.info("Tapılan heyvan qeydə alındı. ID=" + saved.getId());
        return PetDTO.fromEntity(saved, baseUrl);
    }

    // ------------------------------------------------
    // ŞƏKİL İLƏ UYĞUNLUQ AXTARIŞI — Thread ilə parallel
    // ------------------------------------------------

    public MatchResultDTO<PetDTO> findMatches(MultipartFile searchImage,
                                               Pet.PetStatus searchIn,
                                               String baseUrl) throws IOException {
        validateImage(searchImage);
        String searchHash;
        try (InputStream is = searchImage.getInputStream()) {
            searchHash = imageHashService.calculateHash(is);
        }

        List<Pet> candidates = petRepository.findByStatus(searchIn);
        List<Future<MatchResultDTO.MatchEntry<PetDTO>>> futures = new ArrayList<>();

        // Hər heyvanı ayrı thread-də yoxla
        for (Pet candidate : candidates) {
            Future<MatchResultDTO.MatchEntry<PetDTO>> future = executor.submit(() -> {
                long   distance   = imageHashService.hammingDistance(searchHash, candidate.getImageHash());
                double similarity = imageHashService.toSimilarityPercent(distance);
                if (distance <= threshold) {
                    return new MatchResultDTO.MatchEntry<>(
                            PetDTO.fromEntity(candidate, baseUrl), similarity, distance);
                }
                return null;
            });
            futures.add(future);
        }

        // Nəticələri topla
        List<MatchResultDTO.MatchEntry<PetDTO>> matches = new ArrayList<>();
        for (Future<MatchResultDTO.MatchEntry<PetDTO>> future : futures) {
            try {
                MatchResultDTO.MatchEntry<PetDTO> entry = future.get(5, TimeUnit.SECONDS);
                if (entry != null) matches.add(entry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException | TimeoutException e) {
                log.warning("Thread xətası: " + e.getMessage());
            }
        }

        // Oxşarlığa görə sırala (azalan)
        matches.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));

        return matches.isEmpty()
                ? MatchResultDTO.noMatch(candidates.size())
                : MatchResultDTO.withMatches(matches, candidates.size());
    }

    // ------------------------------------------------
    // SİYAHI METODLARI
    // ------------------------------------------------

    public List<PetDTO> getAllLostPets(String baseUrl) {
        List<Pet> pets = petRepository.findByStatus(Pet.PetStatus.LOST);
        List<PetDTO> result = new ArrayList<>();
        for (Pet pet : pets) result.add(PetDTO.fromEntity(pet, baseUrl));
        return result;
    }

    public List<PetDTO> getAllFoundPets(String baseUrl) {
        List<Pet> pets = petRepository.findByStatus(Pet.PetStatus.FOUND);
        List<PetDTO> result = new ArrayList<>();
        for (Pet pet : pets) result.add(PetDTO.fromEntity(pet, baseUrl));
        return result;
    }

    public String getImagePath(Long petId) {
        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) throw new PetNotFoundException(petId);
        return pet.getImagePath();
    }

    // ------------------------------------------------
    // PRIVATE KÖMƏKÇI METODLAR
    // ------------------------------------------------

    private String saveImage(MultipartFile file, String uploadDir) throws IOException {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String ext      = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        Path   path     = dir.resolve(filename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return path.toString();
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Şəkil boş ola bilməz!");
        String type = file.getContentType();
        if (type == null || (!type.equals("image/jpeg") && !type.equals("image/png")))
            throw new IllegalArgumentException("Yalnız JPG və PNG qəbul edilir!");
        if (file.getSize() > 10L * 1024 * 1024)
            throw new IllegalArgumentException("Şəkil 10MB-dan böyük ola bilməz!");
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
