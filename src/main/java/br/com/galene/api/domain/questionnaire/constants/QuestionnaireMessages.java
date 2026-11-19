package br.com.galene.api.domain.questionnaire.constants;

public final class QuestionnaireMessages {

    public static final String QUESTIONNAIRE_FOUND = "Questionário de perfil";
    public static final String ANSWERS_FOUND = "Respostas do questionário";
    public static final String PROFILE_READY = "Seu perfil sonoro está pronto";
    public static final String PROFILE_FOUND = "Perfil sonoro";

    public static final String QUESTION_NOT_FOUND = "Pergunta não encontrada: %d";
    public static final String DUPLICATED_QUESTION = "A pergunta %d foi enviada mais de uma vez";
    public static final String OPTION_NOT_IN_QUESTION = "A opção %d não pertence à pergunta %d";
    public static final String QUESTION_NOT_ANSWERED = "Responda a pergunta %d: %s";
    public static final String MIN_CHOICES = "A pergunta %d pede pelo menos %d opção(ões)";
    public static final String CHOICES_RANGE = "A pergunta %d aceita de %d a %d opções";
    public static final String EXACT_CHOICES = "A pergunta %d aceita exatamente %d opção";
    public static final String EXCLUSIVE_OPTION = "A opção \"%s\" não pode ser marcada junto com outras";
    public static final String QUESTIONNAIRE_NOT_ANSWERED = "Responda o questionário para montar o seu perfil sonoro";
    public static final String PATIENT_NOT_FOUND = "Usuário sem cadastro de paciente";

    private QuestionnaireMessages() { }
}
