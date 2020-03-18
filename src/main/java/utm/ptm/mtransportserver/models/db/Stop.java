package utm.ptm.mtransportserver.models.db;


import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "Stop")
@Table(name="stops")
public class Stop implements Serializable, Comparable<Stop> {
    /*
     *
     * Relations: Two 1-1 relations to unify a node that's on the way with a node that represents the stop
     *
     * Node(1, x, y) -> node on the way
     * Node(2, a, b) -> node that represents transport stop
     * Stop(1, "stop name", 2)
     */

    @Id
    @Column
    private long id;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    private Node stopNode;

    @Column
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "route_node_id", referencedColumnName = "id")
    private Node routeNode;

    @Override
    public int hashCode() {
        int hash = super.hashCode();

        hash += stopNode.hashCode();
        hash += routeNode.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Stop)) {
            return false;
        }

        Stop stop = (Stop) obj;
        if (this.stopNode.equals(stop.stopNode) && this.routeNode.equals(stop.routeNode)) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(Stop stop) {
        return stopNode.compareTo(stop.stopNode);
    }
}
