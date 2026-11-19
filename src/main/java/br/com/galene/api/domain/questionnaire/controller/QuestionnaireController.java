package br.com.galene.api.domain.questionnaire.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.questionnaire.constants.QuestionnaireMessages;
import br.com.galene.api.domain.questionnaire.dto.AnswerItemDto;
import br.com.galene.api.domain.questionnaire.dto.QuestionDto;
import br.com.galene.api.domain.questionnaire.dto.QuestionnaireAnswersDto;
import br.com.galene.api.domain.questionnaire.dto.SoundProfileDto;
import br.com.galene.api.domain.questionnaire.service.QuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Questionário")
@SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
@RestController
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @Operation(summary = "Lista as perguntas do questionário de perfil",
            description = "A pergunta opcional de dados de saúde só aparece com o consentimento HEALTH_DATA")
    @GetMapping(ApiPaths.QUESTIONNAIRE)
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getQuestionnaire(@AuthenticationPrincipal UserSecurity user) {
        var questions = questionnaireService.getQuestionnaire(user.userId());
        return ResponseEntity.ok(new SuccessResult<>(QuestionnaireMessages.QUESTIONNAIRE_FOUND, questions));
    }

    @Operation(summary = "Consulta as respostas atuais do usuário")
    @GetMapping(ApiPaths.USERS_ME_QUESTIONNAIRE)
    public ResponseEntity<ApiResponse<List<AnswerItemDto>>> getAnswers(@AuthenticationPrincipal UserSecurity user) {
        var answers = questionnaireService.getAnswers(user.userId());
        return ResponseEntity.ok(new SuccessResult<>(QuestionnaireMessages.ANSWERS_FOUND, answers));
    }

    @Operation(summary = "Responde o questionário e devolve o perfil sonoro", description = "Substitui as respostas anteriores")
    @PutMapping(ApiPaths.USERS_ME_QUESTIONNAIRE)
    public ResponseEntity<ApiResponse<SoundProfileDto>> saveAnswers(@AuthenticationPrincipal UserSecurity user,
                                                                    @RequestBody @Valid QuestionnaireAnswersDto request) {
        var profile = questionnaireService.saveAnswers(user.userId(), request);
        return ResponseEntity.ok(new SuccessResult<>(QuestionnaireMessages.PROFILE_READY, profile));
    }

    @Operation(summary = "Consulta o perfil sonoro calculado a partir das respostas")
    @GetMapping(ApiPaths.USERS_ME_PROFILE_SOUND)
    public ResponseEntity<ApiResponse<SoundProfileDto>> getSoundProfile(@AuthenticationPrincipal UserSecurity user) {
        var profile = questionnaireService.getSoundProfile(user.userId());
        return ResponseEntity.ok(new SuccessResult<>(QuestionnaireMessages.PROFILE_FOUND, profile));
    }
}
