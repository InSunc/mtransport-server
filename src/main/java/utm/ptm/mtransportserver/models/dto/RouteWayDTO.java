package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utm.ptm.mtransportserver.models.db.RouteWay;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RouteWayDTO {
    private Long id;
    private RouteDTO route;
    private WayDTO way;

    public RouteWayDTO(RouteWay routeWay) {
        id = routeWay.getId();
        route = new RouteDTO(routeWay.getRoute());
        way = new WayDTO(routeWay.getWay());
    }
}
