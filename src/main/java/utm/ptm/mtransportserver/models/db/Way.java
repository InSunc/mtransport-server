package utm.ptm.mtransportserver.models.db;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ways")
public class Way {
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "geometry")
    private LineString points;


    public Way(Long id, String name) {
        this.id = id;
        this.name = name;
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Way way = (Way) o;
        return Objects.equals(id, way.id) &&
                Objects.equals(name, way.name) &&
                Objects.equals(points, way.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points);
    }
}
