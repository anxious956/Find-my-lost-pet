package com.penceui.controller;

import com.penceui.model.PetModel;
import com.penceui.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class AddPetController implements Initializable {

    private static final Logger log = Logger.getLogger(AddPetController.class.getName());

    @FXML private Label lblTitle;
    @FXML private Label lblSubtitle;
    @FXML private Label lblOwnerNameLabel;
    @FXML private VBox dropZone;
    @FXML private Label dropIcon;
    @FXML private Label dropText;
    @FXML private Label lblImageName;
    @FXML private ComboBox<String> comboPetType;
    @FXML private TextField txtOwnerName;
    @FXML private TextField txtOwnerPhone;
    @FXML private TextField txtLocation;
    @FXML private TextArea txtDescription;
    @FXML private Button btnSubmit;
    @FXML private Label lblStatus;

    private String petStatus; // "LOST" or "FOUND"
    private File selectedImageFile;
    private MainController mainController;
    private final ApiService apiService = ApiService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Heyvan növlərini doldur
        comboPetType.getItems().addAll("DOG", "CAT", "BIRD", "RABBIT", "OTHER");
        comboPetType.setPromptText("Heyvan növünü seçin...");
    }

    // MainController tərəfindən çağrılır
    public void setup(String petStatus, MainController mainController) {
        this.petStatus = petStatus;
        this.mainController = mainController;

        if ("LOST".equals(petStatus)) {
            lblTitle.setText("🔴 İtmiş Heyvan İlanı");
            lblSubtitle.setText("İtmiş heyvanınız haqqında məlumat daxil edin");
            lblOwnerNameLabel.setText("Sahibin Adı *");
        } else {
            lblTitle.setText("🟢 Tapılan Heyvan İlanı");
            lblSubtitle.setText("Tapdığınız heyvan haqqında məlumat daxil edin");
            lblOwnerNameLabel.setText("Sizin Adınız *");
        }
    }

    // ── ŞƏKİL SEÇİM ──

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Şəkil seçin");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Şəkil faylları", "*.jpg", "*.jpeg", "*.png")
        );

        File file = fileChooser.showOpenDialog(dropZone.getScene().getWindow());
        if (file != null) {
            setSelectedImage(file);
        }
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            dropZone.getStyleClass().add("drop-zone-active");
        }
        event.consume();
    }

    @FXML
    private void handleDragDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            File file = files.get(0);
            String name = file.getName().toLowerCase();
            if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                setSelectedImage(file);
            } else {
                showStatus("❌ Yalnız JPG və PNG qəbul edilir!", false);
            }
        }
        dropZone.getStyleClass().remove("drop-zone-active");
        event.setDropCompleted(true);
        event.consume();
    }

    private void setSelectedImage(File file) {
        selectedImageFile = file;
        dropIcon.setText("✅");
        dropText.setText("Şəkil seçildi:");
        lblImageName.setText(file.getName());
        lblImageName.setVisible(true);
    }

    // ── FORM GÖNDƏR ──

    @FXML
    private void submitForm() {
        // Validasiya
        if (selectedImageFile == null) {
            showStatus("❌ Zəhmət olmasa şəkil seçin!", false);
            return;
        }
        if (comboPetType.getValue() == null) {
            showStatus("❌ Zəhmət olmasa heyvan növünü seçin!", false);
            return;
        }
        if (txtOwnerName.getText().trim().isEmpty()) {
            showStatus("❌ Ad sahəsi boş ola bilməz!", false);
            return;
        }
        if (txtOwnerPhone.getText().trim().isEmpty()) {
            showStatus("❌ Telefon sahəsi boş ola bilməz!", false);
            return;
        }

        // Formu deaktiv et
        btnSubmit.setDisable(true);
        showStatus("⏳ Göndərilir...", true);

        String name        = txtOwnerName.getText().trim();
        String phone       = txtOwnerPhone.getText().trim();
        String petType     = comboPetType.getValue();
        String description = txtDescription.getText().trim();
        String location    = txtLocation.getText().trim();

        // API çağrısı — Thread-də
        CompletableFuture.supplyAsync(() -> {
            try {
                if ("LOST".equals(petStatus)) {
                    return apiService.registerLostPet(
                            selectedImageFile, name, phone, petType, description, location);
                } else {
                    return apiService.registerFoundPet(
                            selectedImageFile, name, phone, petType, description, location);
                }
            } catch (Exception e) {
                log.warning("API xətası: " + e.getMessage());
                return null;
            }
        }).thenAcceptAsync(result -> Platform.runLater(() -> {
            btnSubmit.setDisable(false);
            if (result != null) {
                showStatus("✅ İlan uğurla əlavə edildi! ID: " + result.getId(), true);
                clearForm();
                // 2 saniyə sonra dashboard-a qayıt
                new Thread(() -> {
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                    Platform.runLater(() -> mainController.goToDashboard());
                }).start();
            } else {
                showStatus("❌ Xəta baş verdi. Backend işləyirmi?", false);
            }
        }));
    }

    @FXML
    private void goBack() {
        mainController.goToDashboard();
    }

    // ── KÖMƏKÇİ ──

    private void showStatus(String message, boolean success) {
        lblStatus.setText(message);
        lblStatus.setStyle(success
                ? "-fx-text-fill: #3fb950; -fx-font-size: 13px;"
                : "-fx-text-fill: #f85149; -fx-font-size: 13px;");
        lblStatus.setVisible(true);
    }

    private void clearForm() {
        selectedImageFile = null;
        dropIcon.setText("📷");
        dropText.setText("Şəkil seçmək üçün klikləyin");
        lblImageName.setVisible(false);
        comboPetType.setValue(null);
        txtOwnerName.clear();
        txtOwnerPhone.clear();
        txtLocation.clear();
        txtDescription.clear();
    }
}
