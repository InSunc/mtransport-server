package utm.ptm.mtransportserver.controllers;

import com.google.gson.Gson;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.RouteDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.models.dto.TripDTO;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.services.TripService;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private StopService stopService;


    @PostMapping
    public ResponseEntity<String> findTrips(@RequestBody CoordinateDTO[] originAndDestination) {

        Stop originStop      = stopService.findNearest(CoordinateDTO.toPoint(originAndDestination[0]));
        Stop destinationStop = stopService.findNearest(CoordinateDTO.toPoint(originAndDestination[1]), originStop.getSide());

        System.out.println(originStop.getId() + ": " + originStop.getName() + " -> " + originStop.getWay().getPointsOrder() + ": " + originStop.getWay().getId());
        System.out.println(destinationStop.getId() + ": " + destinationStop.getName() + " -> " + destinationStop.getWay().getPointsOrder() + ": " + destinationStop.getWay().getId());

        HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result = tripService.findTrips(originStop, destinationStop);

        List<TripDTO> tripDTOS = new ArrayList<>();
        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
            LinkedList<RouteDTO> routes = new LinkedList<>();
            LinkedList<StopDTO> stops = new LinkedList<>();
            entry.getKey().forEach(x -> routes.add(new RouteDTO(x)));
            entry.getValue().forEach(x -> stops.add(new StopDTO(x)));
            tripDTOS.add(new TripDTO(routes, stops));
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(tripDTOS.toArray(), TripDTO[].class);
        return ResponseEntity.status(HttpStatus.OK).body(jsonString);
    }
}
