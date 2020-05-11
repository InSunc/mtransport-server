package utm.ptm.mtransportserver.controllers;

import com.google.gson.Gson;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.models.db.TransportArrival;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.models.dto.TransportArrivalDTO;
import utm.ptm.mtransportserver.models.dto.TransportArrivalReqDTO;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stops")
public class StopController {

    @Autowired
    private StopService stopService;

    @PostMapping("/nearest")
    public ResponseEntity<StopDTO> getNearestStop(@RequestBody CoordinateDTO coordinateDTO) throws IOException {
        Stop stop = stopService.findNearest(CoordinateDTO.toPoint(coordinateDTO));
        StopDTO stopDTO = new StopDTO(stop);
        return ResponseEntity.status(HttpStatus.OK).body(stopDTO);
    }


    @PostMapping
    public ResponseEntity<List<TransportArrivalDTO>> getHourlyPeopleArrival(@RequestBody TransportArrivalReqDTO request) {
        List<TransportArrival> taList = stopService.getTransportArrivals(request.stopId, request.routeId);
        System.out.println(" >>> STATISTICS + " + request.stopId + " - " + request.routeId);
        List<TransportArrivalDTO> taDtos = new ArrayList<>();
        taList.forEach(ta -> taDtos.add(new TransportArrivalDTO(ta)));

//        Gson gson = new Gson();
//        String response = gson.toJson(taList.toArray(), TransportArrivalDTO[].class);

        return ResponseEntity.status(HttpStatus.OK).body(taDtos);
    }
}
