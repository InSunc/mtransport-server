package utm.ptm.mtransportserver.models.db;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity(name = "Stop")
@Table(name="stops")
public class Stop implements Serializable {
    @Id
    @Column
    private long id;

    @Column
    private String name;

    @Column(columnDefinition = "geometry")
    private Point location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Way way;

    @Column
    private int side;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return id == stop.id &&
                Objects.equals(name, stop.name) &&
                Objects.equals(location, stop.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location);
    }

    public int computeSide() {
        Point startPoint;
        Point endPoint;
        if (way.getPointsOrder() >= 0) {
            startPoint = way.getPoints().getStartPoint();
            endPoint = way.getPoints().getEndPoint();
        } else {
            endPoint = way.getPoints().getStartPoint();
            startPoint = way.getPoints().getEndPoint();
        }
        double norm = (endPoint.getX() - startPoint.getX())*(startPoint.getY() - location.getY())
                - (startPoint.getX() - location.getX())*(endPoint.getY() - startPoint.getY());

        if (norm > 0) return 1;
        if (norm < 0) return -1;

        return 0;
    }
}
