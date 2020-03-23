package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.RouteDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.models.dto.WayDTO;
import utm.ptm.mtransportserver.services.NodeService;
import utm.ptm.mtransportserver.services.RouteService;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.services.WayService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private StopService stopService;

    @Autowired
    private WayService wayService;

    @GetMapping
    public Map<String, String> getRoutes() {
        Map<String, String> routes = new HashMap<>();
        routeService.getAll().forEach(route -> routes.put(route.getId(), route.getName()));

        return routes;
    }

    @GetMapping("/{route}")
    public RouteDTO getRoute(@PathVariable(name = "route") String routeId) {
        routeId = routeId.toUpperCase();
        Route route = routeService.getRoute(routeId).get();

        List<Way> ways = wayService.getRouteWays(route);

        List<WayDTO> wayDTOS = new ArrayList<>();
        for (Way way : ways) {
            List<CoordinateDTO> points = new ArrayList<>();
            List<Node> nodes = nodeService.getWayNodes(way);
            nodes.forEach(node -> points.add(new CoordinateDTO(node.getLocation())));
            wayDTOS.add(new WayDTO(way.getName(), points));
        }

        List<Stop> stops = stopService.getByRoute(route);
        List<StopDTO> stopDTOS = new ArrayList<>();
        stops.forEach(stop -> stopDTOS.add(
                new StopDTO(stop.getName(),
                new CoordinateDTO(stop.getStopNode().getLocation()))));

        return new RouteDTO(wayDTOS, stopDTOS);
    }
}
