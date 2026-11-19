package br.com.galene.api.domain.identity.constants;

public final class ConsentMessages {

    public static final String TERMS_FOUND = "Termos vigentes";
    public static final String CONSENTS_FOUND = "Consentimentos do usuário";
    public static final String CONSENTS_ACCEPTED = "Consentimentos registrados";

    public static final String TERM_NOT_FOUND = "Termo não publicado: ";
    public static final String CONSENT_NOT_REVOCABLE = "O termo de uso da conta não pode ser revogado. Para isso, exclua a conta";
    public static final String CONSENT_REQUIRED = "É necessário aceitar o termo %s para usar este recurso";
    public static final String PATIENT_NOT_FOUND = "Usuário sem cadastro de paciente";

    private ConsentMessages() { }
}
