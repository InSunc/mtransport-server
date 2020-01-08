package utm.ptm.mtransportserver.models.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteNode;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RouteNodeDTO {
    Long id;
    RouteDTO route;
    NodeDTO node;

    public RouteNodeDTO(RouteNode routeNode) {
        id = routeNode.getId();
        route = new RouteDTO(routeNode.getRoute());
        node = new NodeDTO(routeNode.getNode());
    }
}
