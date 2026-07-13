package api.poja.app.repository;

import api.poja.app.repository.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, String> {
}