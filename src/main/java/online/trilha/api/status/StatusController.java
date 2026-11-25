package online.trilha.api.status;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/** Rota pública para conferir se a API está no ar e qual perfil ela carregou. */
@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

    @Value("${spring.profiles.active}")
    private String perfil;

    @GetMapping
    public StatusDto status() {
        return new StatusDto("no ar", perfil, Instant.now().toString());
    }

    public record StatusDto(String situacao, String perfil, String momento) {
    }
}
