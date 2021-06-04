package net.cryptic_game.backend.data.sql.entities.server_management;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "disabled_endpoints")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DisabledEndpoint extends TableModel {

    @Id
    @Column(name = "endpoint_path", unique = true, nullable = false)
    private String endpointPath;
}
