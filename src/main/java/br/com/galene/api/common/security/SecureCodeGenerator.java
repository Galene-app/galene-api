package br.com.galene.api.common.security;

public interface SecureCodeGenerator {

    String numericCode(int digits);

    String opaqueToken(int randomBytes);
}
