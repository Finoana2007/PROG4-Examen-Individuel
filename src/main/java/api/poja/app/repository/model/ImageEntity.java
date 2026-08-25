package api.poja.app.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity {
  @Id private String id;
  private String fileName;
  private String email;
  private String status;
  private String bwS3Key;
  private LocalDateTime createdAt;
}
