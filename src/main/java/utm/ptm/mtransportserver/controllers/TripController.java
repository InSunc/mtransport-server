package utm.ptm.mtransportserver.controllers;

import com.google.gson.Gson;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.RouteDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.models.dto.TripDTO;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.services.TripService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private StopService stopService;

    @PostMapping
    public ResponseEntity<String> findTrips(@RequestBody CoordinateDTO[] originAndDestination) throws IOException {

        Stop originStop = stopService.findNearest(CoordinateDTO.toPoint(originAndDestination[0]));
        Stop destinationStop = stopService.findNearest(CoordinateDTO.toPoint(originAndDestination[1]), originStop.getSide());

        System.out.println(originStop.getId() + ": " + originStop.getName() + " : " + originStop.getSide() + " -> " + originStop.getWay().getPointsOrder() + ": " + originStop.getWay().getId());
        System.out.println(destinationStop.getId() + ": " + destinationStop.getName() + " : " + destinationStop.getSide() + " -> " + destinationStop.getWay().getPointsOrder() + ": " + destinationStop.getWay().getId());

        List<TripDTO> tripDTOS = new ArrayList<>();

        if (tripService.tripWasAnalysed(originStop, destinationStop)) {
            tripDTOS = tripService.readTripFromFile(originStop, destinationStop);
        } else {
            HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> results = tripService.findTripStops(originStop, destinationStop);
            ConcurrentHashMap<LinkedHashSet<Route>, LinkedHashSet<Way>> tripWays = tripService.findTripWays(results);
            for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : results.entrySet()) {
                float time = tripService.getTripTime(tripWays.get(entry.getKey()), originStop.getSide());
                System.out.println(" > Trip time = " + time);
                float cost = tripService.getTripCost(entry.getKey());
                tripDTOS.add(new TripDTO(entry.getKey(), entry.getValue(), tripWays.get(entry.getKey()), time, cost));
            }

            tripService.saveTripToFile(originStop, destinationStop, tripDTOS);
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(tripDTOS.toArray(), TripDTO[].class);
        return ResponseEntity.status(HttpStatus.OK).body(jsonString);
    }
}
