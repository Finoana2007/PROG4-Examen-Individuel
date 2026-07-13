package api.poja.app.service;

import api.poja.app.file.bucket.BucketComponent;
import api.poja.app.mail.Email;
import api.poja.app.mail.Mailer;
import api.poja.app.repository.ImageRepository;
import api.poja.app.repository.model.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ImageAsyncService {

    private final BucketComponent bucketComponent;
    private final ImageRepository imageRepository;
    private final Mailer mailer;

    @Async
    @SneakyThrows
    public void processImageAsync(String id, byte[] imageBytes, String originalFileName, String recipientEmail) {
        log.info("Debut traitement asynchrone pour {}", id);
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (original == null) {
                throw new IllegalArgumentException("Format d'image non supporte");
            }

            BufferedImage bwImage = new BufferedImage(
                    original.getWidth(),
                    original.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY
            );
            Graphics g = bwImage.getGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();

            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            File bwFile = File.createTempFile("bw-" + id, "." + extension);
            ImageIO.write(bwImage, extension, bwFile);

            String bucketKey = "bw-images/" + id + "." + extension;
            bucketComponent.upload(bwFile, bucketKey);

            String presignedUrl = bucketComponent.presign(bucketKey, Duration.ofMinutes(10)).toString();

            ImageEntity entity = imageRepository.findById(id).orElseThrow();
            entity.setBwS3Key(bucketKey);
            entity.setStatus("COMPLETED");
            imageRepository.save(entity);

            Email email = new Email(
                    (jakarta.mail.internet.InternetAddress) List.of(recipientEmail),
                    null,
                    null,
                    "Votre image en noir et blanc",
                    "Téléchargez votre image transformée : <a href=\"" + presignedUrl + "\">Cliquez ici</a>",
                    List.of()
            );
            mailer.accept(email);

            Files.deleteIfExists(bwFile.toPath());
            log.info("Traitement termine pour {}", id);

        } catch (Exception e) {
            log.error("Erreur lors du traitement de {}", id, e);
            ImageEntity entity = imageRepository.findById(id).orElseThrow();
            entity.setStatus("FAILED");
            imageRepository.save(entity);
        }
    }
}