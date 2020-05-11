package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
public class TripDTO {
    public List<RouteDTO> routes;
    public List<StopDTO> stops;
    public List<WayDTO> ways;
    public float time;
    public float cost;

    public TripDTO(LinkedHashSet<Route> routes, LinkedHashSet<Stop> stops, LinkedHashSet<Way> ways) {
        this.routes = new ArrayList<>();
        this.stops = new ArrayList<>();
        this.ways = new ArrayList<>();
        routes.forEach(x -> this.routes.add(new RouteDTO(x)));
        stops.forEach(x -> this.stops.add(new StopDTO(x)));
        ways.forEach(x -> this.ways.add(new WayDTO(x)));
    }

    public TripDTO(LinkedHashSet<Route> routes, LinkedHashSet<Stop> stops, LinkedHashSet<Way> ways, float time, float cost) {
        this.routes = new ArrayList<>();
        this.stops = new ArrayList<>();
        this.ways = new ArrayList<>();
        routes.forEach(x -> this.routes.add(new RouteDTO(x)));
        stops.forEach(x -> this.stops.add(new StopDTO(x)));
        ways.forEach(x -> this.ways.add(new WayDTO(x)));
        this.time = time;
        this.cost = cost;
    }


    public TripDTO(LinkedList<RouteDTO> routes, LinkedList<StopDTO> stops) {
        this.routes = routes;
        this.stops = stops;
    }
}
