package utm.ptm.mtransportserver.controllers;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import java.io.FileNotFoundException;
import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stops")
public class StopController {

    @Autowired
    private StopService stopService;

    @PostMapping
    public ResponseEntity<StopDTO> nearest(@RequestBody CoordinateDTO coordinateDTO) throws IOException {
        Stop stop = stopService.findNearest(CoordinateDTO.toPoint(coordinateDTO));
        StopDTO stopDTO = new StopDTO(stop);
        return ResponseEntity.status(HttpStatus.OK).body(stopDTO);
    }
}
