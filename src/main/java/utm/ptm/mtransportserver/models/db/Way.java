package utm.ptm.mtransportserver.models.db;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    @Column
    private boolean bidirectional;


    public Way(Long id, String name) {
        this.id = id;
        this.name = name;
    };


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Way)) return false;
        Way way = (Way) o;
        return bidirectional == way.isBidirectional() &&
                id.equals(way.getId()) &&
                Objects.equals(name, way.getName()) &&
                Objects.equals(points, way.getPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points, bidirectional);
    }

    public int getPointsOrder() {
        Coordinate[] coordinates = points.getCoordinates();
        int lastIndex = coordinates.length - 1;

        if (coordinates[0].x < coordinates[lastIndex].x) return 1;
        if (coordinates[0].x > coordinates[lastIndex].x) return -1;
        if (coordinates[0].y < coordinates[lastIndex].y) return 1;
        if (coordinates[0].y > coordinates[lastIndex].y) return -1;

        System.err.println(" !!! WAY ORDER == 0");
        return 0;
    }
}
