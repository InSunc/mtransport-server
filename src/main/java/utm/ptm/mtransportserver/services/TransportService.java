package utm.ptm.mtransportserver.services;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.repositories.TransportRepository;

import javax.sound.sampled.Line;
import java.util.*;

@Service
public class TransportService {

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private WayService wayService;

    @Autowired
    private MqttService mqttService;


    private void sort(List<Coordinate> coordinates) {
        Collections.reverse(coordinates);
        Collections.sort(coordinates, new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate a, Coordinate b) {
                return a.compareTo(b);
            }
        });
        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate a = coordinates.get(i);
            // Find the closest.
            int closest = i;
            double howClose = Double.MAX_VALUE;
            for (int j = i + 1; j < coordinates.size(); j++) {
                double howFar = a.distance(coordinates.get(j));
                if (howFar < howClose) {
                    closest = j;
                    howClose = howFar;
                }
            }
            if (closest != i + 1) {
                // Swap it in.
                Collections.swap(coordinates, i + 1, closest);
            }
        }
    }

    public void simulate(String routeId) {
        Route route = routeService.getRoute(routeId).get();
        Transport transport = new Transport();
        transport.setRoute(route);
        int transportId = new Random().nextInt();
        transport.setId(transportId);
        transport = transportRepository.save(transport);

        List<Way> ways = wayService.getRouteWays(route);
        List<Coordinate> coordinates = new ArrayList<>();
        ways.forEach(way -> coordinates.addAll(Arrays.asList(way.getWayNodes().getCoordinates())));

        sort(coordinates);

        for (Coordinate coordinate : coordinates) {
            String data = "{\"board\": " + transportId
                    + ", \"latitude\": " + coordinate.y
                    + ", \"longitude\": " + coordinate.x
                    + "}";
            mqttService.publish(routeId, data);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
