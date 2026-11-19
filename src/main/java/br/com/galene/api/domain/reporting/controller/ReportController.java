package br.com.galene.api.domain.reporting.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.reporting.constants.ReportMessages;
import br.com.galene.api.domain.reporting.dto.DailyReportDto;
import br.com.galene.api.domain.reporting.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Relatórios")
@SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Relatório diário: tempo em cada nível, média (Leq), pico e locais nos níveis Intenso e Crítico",
            description = "Sem data, é o dia anterior. Outros dias exigem Premium. Disponível após 24h de uso. "
                    + "Somente no ambiente de desenvolvimento, testMode=true ignora as 24h de uso, aceita o dia de hoje e dispensa o Premium")
    @GetMapping(ApiPaths.USERS_ME_REPORTS + ApiPaths.REPORT_DAILY)
    public ResponseEntity<ApiResponse<DailyReportDto>> getDailyReport(
            @AuthenticationPrincipal UserSecurity user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean testMode) {
        var report = reportService.getDailyReport(user.userId(), date, testMode);
        return ResponseEntity.ok(new SuccessResult<>(ReportMessages.DAILY_REPORT_FOUND, report));
    }

    @Operation(summary = "Relatórios semanal e mensal (Premium, fora do MVP)")
    @GetMapping(ApiPaths.USERS_ME_REPORTS + ApiPaths.REPORT_PREMIUM_PERIOD)
    public ResponseEntity<ApiResponse<Void>> getPremiumReport(@AuthenticationPrincipal UserSecurity user, @PathVariable String period) {
        reportService.requirePremiumPeriod();
        return ResponseEntity.noContent().build();
    }
}
