package utm.ptm.mtransportserver.controllers;

import com.google.gson.Gson;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.ClientStopArrival;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.models.db.TransportArrival;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.models.dto.TransportArrivalDTO;
import utm.ptm.mtransportserver.models.dto.TransportArrivalReqDTO;
import utm.ptm.mtransportserver.services.StopService;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import javax.xml.transform.OutputKeys;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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

        return ResponseEntity.status(HttpStatus.OK).body(taDtos);
    }

    @GetMapping("/{stopId}")
    public ResponseEntity<HashMap<Integer, Integer>> getNrOfPeople(@PathVariable(name = "stopId") long stopId) {
        HashMap<Integer, Integer> result = new HashMap<>(); // hour: nrOfPeople
        List<ClientStopArrival> clients = stopService.getCLientArrivals(stopId);
        if (clients == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Integer> hours = new ArrayList<>();
        for (int i = 6; i < 24; i++) {
            result.put(i, 0);
        }

        System.out.println(" > > > " + clients.size());

        for (ClientStopArrival client : clients) {
            LocalDateTime timestamp = client.getTimestamp();
            boolean fromToday = timestamp.toLocalDate().isEqual(LocalDate.now());
            if (fromToday) {
                int hour = timestamp.getHour();
                System.out.println(" > > >  IT's TODAY");
                if (client.isArrived()) {
                    result.put(hour, result.get(hour) + 1);
                } else {
                    result.put(hour, result.get(hour) - 1);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
