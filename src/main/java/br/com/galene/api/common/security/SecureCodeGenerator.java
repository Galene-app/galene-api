package br.com.galene.api.common.security;

/**
 * Gera códigos e tokens de uso único (2FA, recuperação de senha). É uma interface para que
 * os testes possam fixar o valor gerado.
 */
public interface SecureCodeGenerator {

    String numericCode(int digits);

    String opaqueToken(int randomBytes);
}
