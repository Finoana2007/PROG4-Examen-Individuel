package api.poja.app.repository.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ImageEntity {
    @Id private String id;
    private String fileName;
    private String email;
    private String status;
    private String bwS3Key;
    private LocalDateTime createdAt;
}