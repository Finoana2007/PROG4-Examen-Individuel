package api.poja.app.endpoint.rest.controller;

import api.poja.app.repository.ImageRepository;
import api.poja.app.repository.model.ImageEntity;
import api.poja.app.service.ImageAsyncService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ImageController {

  private final ImageRepository repository;
  private final ImageAsyncService asyncService;

  // 1. POST /images – soumettre une image
  @PostMapping("/images")
  public ResponseEntity<ImageResponse> submitImage(
          @RequestParam("file") MultipartFile file,
          @RequestParam("email") String email) {

    // Validation du type MIME (JPEG ou PNG)
    String contentType = file.getContentType();
    if (contentType == null ||
            (!contentType.equals(MediaType.IMAGE_JPEG_VALUE) &&
                    !contentType.equals(MediaType.IMAGE_PNG_VALUE))) {
      return ResponseEntity.badRequest()
              .body(new ImageResponse(null, "Seuls JPEG et PNG sont acceptés"));
    }

    // Sauvegarde SYNCHRONE en base (status = PROCESSING)
    String id = UUID.randomUUID().toString();
    ImageEntity entity = ImageEntity.builder()
            .id(id)
            .fileName(file.getOriginalFilename())
            .email(email)
            .status("PROCESSING")
            .createdAt(LocalDateTime.now())
            .build();
    repository.save(entity);

    // Déclenchement ASYNCHRONE du traitement
    try {
      byte[] fileBytes = file.getBytes();
      asyncService.processImageAsync(id, fileBytes, file.getOriginalFilename(), email);
    } catch (IOException e) {
      entity.setStatus("FAILED");
      repository.save(entity);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ImageResponse(id, "Erreur lors de la lecture du fichier"));
    } catch (Exception e) {
      entity.setStatus("FAILED");
      repository.save(entity);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ImageResponse(id, "Erreur lors du traitement asynchrone"));
    }

    // Réponse immédiate (202 ACCEPTED) pour prouver l'Async
    return ResponseEntity.accepted()
            .body(new ImageResponse(id, "Image soumise, traitement en cours"));
  }

  // 2. GET /images – lister toutes les images
  @GetMapping("/images")
  public List<ImageEntity> listImages() {
    return repository.findAll();
  }

  // 3. GET /images/{id} – détails d'une image spécifique
  @GetMapping("/images/{id}")
  public ResponseEntity<ImageEntity> getImage(@PathVariable String id) {
    return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  // DTO pour la réponse
  public record ImageResponse(String id, String message) {}
}