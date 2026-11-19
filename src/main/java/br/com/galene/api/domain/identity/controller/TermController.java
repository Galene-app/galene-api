package br.com.galene.api.domain.identity.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.domain.identity.constants.ConsentMessages;
import br.com.galene.api.domain.identity.dto.consent.TermResponseDto;
import br.com.galene.api.domain.identity.service.ConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Termos")
@RestController
@RequestMapping(ApiPaths.TERMS)
@RequiredArgsConstructor
public class TermController {

    private final ConsentService consentService;

    @Operation(summary = "Lista a versão vigente de cada termo")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TermResponseDto>>> getCurrentTerms() {
        var terms = consentService.getCurrentTerms();
        return ResponseEntity.ok(new SuccessResult<>(ConsentMessages.TERMS_FOUND, terms));
    }
}
