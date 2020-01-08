package utm.ptm.mtransportserver.models.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "route_nodes")
public class RouteNode {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    Node node;
}
