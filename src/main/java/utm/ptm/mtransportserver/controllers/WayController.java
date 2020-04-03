package utm.ptm.mtransportserver.controllers;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.image.ImageWatched;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteWay;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.RouteDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.models.dto.WayDTO;
import utm.ptm.mtransportserver.repositories.WayRepository;
import utm.ptm.mtransportserver.services.RouteService;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.services.WayService;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ways")
public class WayController {
    @Autowired
    private WayRepository wayRepository;

    @Autowired
    private WayService wayService;

    @Autowired
    private RouteService routeService;

    @GetMapping
    public RouteDTO get() {
        List<Way> ways = wayRepository.findWay();
        List<WayDTO> wayDTOS = new ArrayList<>();

        for (Way way : ways) {

            List<Route> routes = routeService.findAllByWay(way);
            for (Route route : routes) {
                System.out.println(" >>> " + route.getId());
            }

            List<CoordinateDTO> coordinateDTOS = new ArrayList<>();
            Coordinate[] coordinates = way.getPoints().getCoordinates();
            for (Coordinate coordinate : coordinates) {
                coordinateDTOS.add(new CoordinateDTO(coordinate));
            }
            wayDTOS.add(new WayDTO(way.getName(), coordinateDTOS));
        }

//        List<Stop> stops = stopService.findByRoute(route);
        List<StopDTO> stopDTOS = new ArrayList<>();
//        stops.forEach(stop -> stopDTOS.add(new StopDTO(stop.getName(), new CoordinateDTO(stop.getLocation()))));

        return new RouteDTO(wayDTOS, stopDTOS);
    }

}





