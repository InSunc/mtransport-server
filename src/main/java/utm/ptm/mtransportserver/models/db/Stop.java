package utm.ptm.mtransportserver.models.db;


import com.google.gson.internal.$Gson$Types;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
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
}
