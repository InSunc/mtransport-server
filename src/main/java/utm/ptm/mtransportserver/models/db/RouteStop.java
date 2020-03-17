package utm.ptm.mtransportserver.models.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "route_stops")
public class RouteStop {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    Stop stop;

    public RouteStop(Route route, Stop stop) {
        this.route = route;
        this.stop = stop;
    }
}
