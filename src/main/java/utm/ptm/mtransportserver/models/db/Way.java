package utm.ptm.mtransportserver.models.db;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;

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
    private LineString wayNodes;


    public Way(Long id, String name) {
        this.id = id;
        this.name = name;
    };
}
