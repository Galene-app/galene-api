package br.com.galene.api.domain.monitoring.constants;

public final class MonitoringMessages {

    public static final String LEVELS_FOUND = "Faixas de ruído";
    public static final String READINGS_SAVED = "Leituras registradas";
    public static final String READINGS_FOUND = "Leituras de ruído";

    public static final String INVALID_READINGS = "Lote recusado: corrija as leituras indicadas e envie novamente";
    public static final String TOO_MANY_READINGS = "Envie no máximo %d leituras por lote";
    public static final String INVALID_PERIOD = "O início do período deve ser anterior ao fim";
    public static final String DB_OUT_OF_RANGE = "O valor deve estar entre 0 e 140 dB";
    public static final String FUTURE_READING = "O horário da leitura não pode ser no futuro";
    public static final String DUPLICATED_IN_BATCH = "Leitura repetida no lote (mesmo horário)";
    public static final String ALREADY_SAVED = "Já existe uma leitura registrada neste horário";
    public static final String LEVEL_NOT_FOUND = "Nenhuma faixa de ruído cobre o valor ";
    public static final String PATIENT_NOT_FOUND = "Usuário sem cadastro de paciente";

    public static final String READING_FIELD = "readings[%d].%s";

    private MonitoringMessages() { }
}
