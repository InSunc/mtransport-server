package utm.ptm.mtransportserver.controllers;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.TripDTO;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.services.TripService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private StopService stopService;


    @PostMapping
    public ResponseEntity<TripDTO> findTrips(@RequestBody CoordinateDTO origin, CoordinateDTO destination) {

        Stop originStop      = stopService.findNearest(CoordinateDTO.toPoint(origin));
        Stop destinationStop = stopService.findNearest(CoordinateDTO.toPoint(destination));

        tripService.findTrips(originStop, destinationStop);

        return ResponseEntity.status(HttpStatus.OK).body(new TripDTO());
    }
}
