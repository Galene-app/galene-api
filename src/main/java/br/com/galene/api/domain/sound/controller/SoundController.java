package br.com.galene.api.domain.sound.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.sound.constants.SoundMessages;
import br.com.galene.api.domain.sound.dto.RecommendedSoundsDto;
import br.com.galene.api.domain.sound.dto.SoundDto;
import br.com.galene.api.domain.sound.enums.SoundType;
import br.com.galene.api.domain.sound.service.SoundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sons")
@RestController
@RequiredArgsConstructor
public class SoundController {

    private final SoundService soundService;

    @Operation(summary = "Catálogo de sons (ruídos branco, rosa e marrom, lo-fi, binaural e natureza)",
            description = "Cada som toca no máximo 1h; para mais tempo o app repete. Filtre por tipo com ?type=")
    @GetMapping(ApiPaths.SOUNDS)
    public ResponseEntity<ApiResponse<List<SoundDto>>> getCatalog(@RequestParam(required = false) SoundType type) {
        var sounds = soundService.getCatalog(type);
        return ResponseEntity.ok(new SuccessResult<>(SoundMessages.SOUNDS_FOUND, sounds));
    }

    @Operation(summary = "Sons mapeados para o perfil do questionário, agrupados por tipo e com o motivo",
            description = "Sons exclusivos do Premium não aparecem para o plano gratuito")
    @SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
    @GetMapping(ApiPaths.USERS_ME_SOUNDS)
    public ResponseEntity<ApiResponse<List<RecommendedSoundsDto>>> getRecommendedSounds(@AuthenticationPrincipal UserSecurity user) {
        var recommendations = soundService.getRecommendedSounds(user.userId());
        return ResponseEntity.ok(new SuccessResult<>(SoundMessages.RECOMMENDED_SOUNDS, recommendations));
    }
}
