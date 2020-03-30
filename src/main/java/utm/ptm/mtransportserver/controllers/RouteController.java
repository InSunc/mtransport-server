package utm.ptm.mtransportserver.controllers;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.RouteDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.models.dto.WayDTO;
import utm.ptm.mtransportserver.services.*;

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

    // TODO: DELETE
    @GetMapping("/slow/{route}")
    public RouteDTO getRoute(@PathVariable(name = "route") String routeId) {

        long startTime = System.nanoTime();

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

        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000; // ms
        System.out.println("=============================================================");
        System.out.println("\t>>>\tgetRoute(" + routeId + ") took " + duration + " ms");
        System.out.println("=============================================================");

        return new RouteDTO(wayDTOS, stopDTOS);
    }

    @GetMapping("/{route}")
    public RouteDTO getRouteFast(@PathVariable(name = "route") String routeId) {

        long startTime = System.nanoTime();

        routeId = routeId.toUpperCase();
        Route route = routeService.getRoute(routeId).get();

        List<Way> ways = wayService.getRouteWays(route);

        List<WayDTO> wayDTOS = new ArrayList<>();
        for (Way way : ways) {
            List<CoordinateDTO> coordinateDTOS = new ArrayList<>();
            Coordinate[] coordinates = way.getWayNodes().getCoordinates();
            for (Coordinate coordinate : coordinates) {
                coordinateDTOS.add(new CoordinateDTO(coordinate));
            }
            wayDTOS.add(new WayDTO(way.getName(), coordinateDTOS));
        }

        List<Stop> stops = stopService.getByRoute(route);
        List<StopDTO> stopDTOS = new ArrayList<>();
        stops.forEach(stop -> stopDTOS.add(
                new StopDTO(stop.getName(),
                        new CoordinateDTO(stop.getStopNode().getLocation()))));

        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000; // ms
        System.out.println("=============================================================");
        System.out.println("\t>>>\tgetRouteFast(" + routeId + ") took " + duration + " ms");
        System.out.println("=============================================================");

        return new RouteDTO(wayDTOS, stopDTOS);
    }


    @Autowired
    private TransportService transportService;
    @Autowired
    private MqttService mqttService;

    @GetMapping("/simulate/{route}")
    public String simulateRoute(@PathVariable(name = "route") String routeId) {
        transportService.simulate(routeId);
        return "Simulation started";
    }

}
