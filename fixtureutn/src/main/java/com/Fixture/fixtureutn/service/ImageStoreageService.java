package com.Fixture.fixtureutn.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStoreageService {
// constructor sin paramentros
    public ImageStoreageService() {}
    // Recibe el archivo crudo y nos devuelve la direccion donde quedo guardado
    public String storeImage(MultipartFile file, String subfolder) throws IOException {

        if (file.isEmpty()) {
            return null;
        }

        // 1. Obtener extensión del archivo
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 2. Generar nombre único (UUID) para que no se pisen las fotos
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 3. RUTAS DINÁMICAS (UNIVERSALES)
        // Esto obtiene la carpeta donde está tu proyecto automáticamente
        String projectDir = System.getProperty("user.dir");

        // Ruta A: Código Fuente (para que la imagen quede guardada físicamente)
        Path uploadPathSrc = Paths.get(projectDir, "src", "main", "resources", "static", "img", subfolder);

        // Ruta B: Target (para que se vea al instante en la web sin reiniciar)
        Path uploadPathTarget = Paths.get(projectDir, "target", "classes", "static", "img", subfolder);

        // 4. Crear carpetas si no existen
        if (!Files.exists(uploadPathSrc)) {
            Files.createDirectories(uploadPathSrc);
        }
        if (!Files.exists(uploadPathTarget)) {
            Files.createDirectories(uploadPathTarget);
        }

        // 5. Guardar en ambos lugares
        try (InputStream inputStream = file.getInputStream()) {
            Path filePathSrc = uploadPathSrc.resolve(uniqueFileName);
            Files.copy(inputStream, filePathSrc, StandardCopyOption.REPLACE_EXISTING);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePathTarget = uploadPathTarget.resolve(uniqueFileName);
            Files.copy(inputStream, filePathTarget, StandardCopyOption.REPLACE_EXISTING);
        }

        // 6. Devolver la ruta relativa para la base de datos
        return "/img/" + subfolder + "/" + uniqueFileName;
    }
}