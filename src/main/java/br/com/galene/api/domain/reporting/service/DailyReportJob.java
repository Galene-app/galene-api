package br.com.galene.api.domain.reporting.service;

import br.com.galene.api.config.shared.ClockConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Log4j2
@Component
@RequiredArgsConstructor
public class DailyReportJob {

    private final ReportService reportService;
    private final Clock clock;

    @Scheduled(cron = "${galene.reports.daily-cron}", zone = ClockConfig.ZONE_ID_NAME)
    public void generateYesterdayReports() {
        var date = LocalDate.now(clock).minusDays(1);
        var patientIds = reportService.findPatientsWithReadings(date);
        int generated = 0;

        for (var patientId : patientIds) {
            try {
                if (reportService.generateDailyReport(patientId, date)) generated++;
            } catch (RuntimeException ex) {
                log.error("Falha ao gerar o relatório de {} do paciente {}", date, patientId, ex);
            }
        }
        log.info("Job D-1 de {}: {} relatório(s) gerado(s) para {} paciente(s) com leituras", date, generated, patientIds.size());
    }
}
