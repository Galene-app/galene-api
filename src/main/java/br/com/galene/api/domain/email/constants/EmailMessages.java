package br.com.galene.api.domain.email.constants;

public final class EmailMessages {

    public static final String SEND_FAILED = "Falha ao enviar o e-mail";
    public static final String INVALID_API_KEY = "Maileroo recusou a chave de API: verifique MAILEROO_API_KEY";
    public static final String COMMUNICATION_FAILED = "Falha na comunicação com o Maileroo";
    public static final String TEMPLATE_NOT_FOUND = "Template de e-mail não cadastrado: ";
    public static final String UNFILLED_PLACEHOLDER = "Template de e-mail com campo não preenchido: ";

    private EmailMessages() { }
}
