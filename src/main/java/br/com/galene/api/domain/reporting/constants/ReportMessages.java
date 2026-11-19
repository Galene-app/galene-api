package br.com.galene.api.domain.reporting.constants;

public final class ReportMessages {

    public static final String DAILY_REPORT_FOUND = "Relatório diário";

    public static final String INVALID_DATE = "Escolha um dia que já terminou";
    public static final String FUTURE_DATE = "O relatório não pode ser de um dia futuro";
    public static final String TEST_MODE_DISABLED = "O modo de teste do relatório só está disponível no ambiente de desenvolvimento";
    public static final String PREMIUM_DAY = "No plano gratuito, o relatório diário é o do dia anterior. Outros dias fazem parte do Premium";
    public static final String PREMIUM_PERIOD = "Relatórios semanais e mensais fazem parte do plano Premium";
    public static final String NOT_ENOUGH_USAGE = "Dados insuficientes: o relatório fica disponível após 24 horas de uso do app";
    public static final String NO_READINGS = "Dados insuficientes para gerar o relatório deste dia";
    public static final String PATIENT_NOT_FOUND = "Usuário sem cadastro de paciente";
    public static final String LEVEL_NOT_FOUND = "Nenhuma faixa de ruído cobre o valor ";

    private ReportMessages() { }
}
