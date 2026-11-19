package br.com.galene.api.domain.email.entity;

import br.com.galene.api.domain.email.enums.EmailType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gln_email_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EmailTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Long id;

    @Enumerated(EnumType.STRING)
    private EmailType templateKey;

    private String subject;

    @Lob
    private String htmlBody;

    @Lob
    private String plainBody;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
