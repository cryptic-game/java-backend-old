package net.cryptic_game.backend.admin.data.sql.entities.website_management.faq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "faq_entry")
public class FAQEntry extends TableModelAutoId {

    @Column(name = "question", updatable = true, nullable = false, unique = false)
    private String question;

    @Column(name = "answer", updatable = true, nullable = false, unique = false)
    private String answer;
}
