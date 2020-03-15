package utm.ptm.mtransportserver.models.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nodes")
@JsonIgnoreProperties(value = {"stop", "stopNode"})
public class Node implements Serializable {
    @Id
    @Column
    private Long id;

    @Column(columnDefinition = "geometry")
    private Point point;


    @JsonIgnore
    @OneToOne(mappedBy = "routeNode", targetEntity = Stop.class)
    private Stop stop;

    @JsonIgnore
    @OneToOne(mappedBy = "stopNode", targetEntity = Stop.class)
    private Stop stopNode;

    public Node(long id, Point point) {
        this.id = id;
        this.point = point;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();

        hash += point.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Node)) {
            return false;
        }

        Node node = (Node) obj;
        if (this.point.equals(node.getPoint())) {
            return true;
        }

        return false;
    }
}
