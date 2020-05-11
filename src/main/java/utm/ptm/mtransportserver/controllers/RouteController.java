package utm.ptm.mtransportserver.controllers;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private StopService stopService;

    @Autowired
    private WayService wayService;


    @GetMapping
    public ResponseEntity<List<RouteDTO>> getRoutes() {
        List<RouteDTO> routeDTOS = new ArrayList<>();

        List<Route> routes = routeService.findAll();
        for (Route route : routes) {
            List<Way> ways = wayService.getRouteWays(route);

            List<Stop> stops = stopService.findByRoute(route);
            List<StopDTO> stopDTOS = new ArrayList<>();
            stops.forEach(stop -> stopDTOS.add(new StopDTO(stop)));
            RouteDTO routeDTO = new RouteDTO(null, stopDTOS);
            routeDTO.id = route.getId();
            routeDTO.name = routeDTO.stops.get(0).name + " - " + routeDTO.stops.get(routeDTO.stops.size() - 1).name;
            routeDTOS.add(routeDTO);
        }

//        routeService.findAll().forEach(route -> routes.add(new RouteDTO(route.getId(), route.getName())));
        return ResponseEntity.status(HttpStatus.OK).body(routeDTOS);
    }


    @GetMapping("/{routeId}")
    public RouteDTO getRoute(@PathVariable(name = "routeId") String routeId) {
        routeId = routeId.toUpperCase();
        Route route = routeService.findById(routeId).get();

        List<Way> ways = wayService.getRouteWays(route);

        List<WayDTO> wayDTOS = new ArrayList<>();
        ways.forEach(way -> wayDTOS.add(new WayDTO(way)));

        List<Stop> stops = stopService.findByRoute(route);
        List<StopDTO> stopDTOS = new ArrayList<>();
        stops.forEach(stop -> stopDTOS.add(new StopDTO(stop.getName(), new CoordinateDTO(stop.getLocation()))));

        return new RouteDTO(wayDTOS, stopDTOS);
    }



//    @GetMapping("/test")
//    public RouteDTO test() {
//        GeometryFactory geometryFactory = new GeometryFactory();
//        Stop oStop = stopService.findNearest(geometryFactory.createPoint(new Coordinate(28.8254946, 46.9899328)));
//        Stop dStop = stopService.findNearest(geometryFactory.createPoint(new Coordinate(28.8674648, 47.0584805)));
//
//        List<Route> oRoutes = routeService.findAllByStop(oStop);
//        List<Route> dRoutes = routeService.findAllByStop(dStop);
//
//
////        oRoutes.forEach(oRoute -> stopsToCheck.addAll(stopService.findByRoute(oRoute)));
//        List<Stop> stopsToCheck = new ArrayList<>();
//        for (Route oRoute : oRoutes) {
//            stopsToCheck.addAll(stopService.findByRoute(oRoute));
//            List<Stop> stopsToDelete = new ArrayList<>();
//
//            for (Stop stop : stopsToCheck) {
//                boolean toDelete = false;
//                for (Route route : dRoutes) {
//                    if (!routeService.findAllByStop(stop).contains(route)) {
//                        stopsToDelete.add(stop);
//                    }
//                }
//            }
//        }
//
//        dRoutes.forEach(dRoute -> stopsToCheck.addAll(stopService.findByRoute(dRoute)));
//
//        for (Route oRoute : oRoutes) {
//            for (Stop stop : stopsToCheck) {
//                List<Route> routes = routeService.findAllByStop(stop);
//                if (!routes.contains(oRoute)) {
//                    stopsToCheck.remove(stop);
//                }
//            }
//        }
//    }
}
