package utm.ptm.mtransportserver.models.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "route")
public class Route {
    @Id
    @Column
    private String id;

    @Column
    private String name;

    public Route(String id) {
        this.id = id;
    }
}
