package utm.ptm.mtransportserver.models.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(id, route.id) &&
                Objects.equals(name, route.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
