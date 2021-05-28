package net.cryptic_game.backend.admin.model.server_management;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.getnova.framework.jpa.model.TableModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_disabled_endpoints")
public class DisabledEndpointModel extends TableModel {

    @Id
    @Column(name = "endpoint_path", unique = true, nullable = false, updatable = false)
    private String endpointPath;

    @Column(name = "reason")
    private String reason;
}
