package utm.ptm.mtransportserver.models.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private double lat;

    @Column
    private double lng;

    @JsonIgnore
    @OneToOne(mappedBy = "routeNode", targetEntity = Stop.class)
    private Stop stop;

    @JsonIgnore
    @OneToOne(mappedBy = "stopNode", targetEntity = Stop.class)
    private Stop stopNode;

    public Node(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();

        hash += lat + lng;

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
        if (this.lat == node.lat && this.lng == node.lng) {
            return true;
        }

        return false;
    }
}
