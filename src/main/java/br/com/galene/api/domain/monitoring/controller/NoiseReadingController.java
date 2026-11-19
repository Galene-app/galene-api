package br.com.galene.api.domain.monitoring.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.PageResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.monitoring.constants.MonitoringMessages;
import br.com.galene.api.domain.monitoring.dto.NoiseLevelDto;
import br.com.galene.api.domain.monitoring.dto.NoiseReadingResponseDto;
import br.com.galene.api.domain.monitoring.dto.NoiseReadingsRequestDto;
import br.com.galene.api.domain.monitoring.dto.NoiseReadingsResultDto;
import br.com.galene.api.domain.monitoring.service.NoiseReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@Tag(name = "Monitoramento de ruído")
@RestController
@RequiredArgsConstructor
public class NoiseReadingController {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final NoiseReadingService noiseReadingService;

    @Operation(summary = "Lista as faixas de ruído e a ação do Galene em cada uma")
    @GetMapping(ApiPaths.NOISE_LEVELS)
    public ResponseEntity<ApiResponse<List<NoiseLevelDto>>> getNoiseLevels() {
        var levels = noiseReadingService.getNoiseLevels();
        return ResponseEntity.ok(new SuccessResult<>(MonitoringMessages.LEVELS_FOUND, levels));
    }

    @Operation(summary = "Recebe um lote de até 500 leituras e classifica cada uma em um nível",
            description = "Se alguma leitura for inválida, o lote inteiro é recusado com a lista de erros")
    @SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
    @PostMapping(ApiPaths.USERS_ME_NOISE_READINGS)
    public ResponseEntity<ApiResponse<NoiseReadingsResultDto>> saveReadings(@AuthenticationPrincipal UserSecurity user,
                                                                            @RequestBody @Valid NoiseReadingsRequestDto request) {
        var result = noiseReadingService.saveReadings(user.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult<>(HttpStatus.CREATED, MonitoringMessages.READINGS_SAVED, result));
    }

    @Operation(summary = "Lista as leituras do usuário no período (padrão: últimas 24h), da mais recente para a mais antiga")
    @SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
    @GetMapping(ApiPaths.USERS_ME_NOISE_READINGS)
    public ResponseEntity<ApiResponse<PageResponse<NoiseReadingResponseDto>>> getReadings(
            @AuthenticationPrincipal UserSecurity user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable) {
        var readings = noiseReadingService.getReadings(user.userId(), from, to, pageable);
        return ResponseEntity.ok(new SuccessResult<>(MonitoringMessages.READINGS_FOUND, readings));
    }
}
