package utm.ptm.mtransportserver.services;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.operation.linemerge.LineSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.MtransportServerApplication;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Ticket;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.repositories.TicketRepository;
import utm.ptm.mtransportserver.repositories.TransportArrivalRepository;
import utm.ptm.mtransportserver.repositories.TransportRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimulationService {

    @Autowired
    private RouteService routeService;

    @Autowired
    private WayService wayService;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private TicketService ticketService;


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

    public void simulateTransport(String routeId) {
        final int SLEEP_TIME = 1000;
        Route route = routeService.findById(routeId).get();
        Transport transport = new Transport();
        transport.setRoute(route);
        int transportId = new Random().nextInt();
        transport.setId(transportId);
        transport = transportService.save(transport);

        List<Way> ways = wayService.getRouteWays(route);

        LineSequencer lineSequencer = new LineSequencer();
        for (Way way : ways) {
            lineSequencer.add(way.getPoints());
        }


        if (lineSequencer.isSequenceable()) {
            for (Coordinate coordinate : lineSequencer.getSequencedLineStrings().getCoordinates()) {
                String data = "{\"board\": " + transportId
                        + ", \"latitude\": " + coordinate.y
                        + ", \"longitude\": " + coordinate.x
                        + "}";
                mqttService.simulate("raw/" + routeId, data);
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            List<Coordinate> coordinates = new ArrayList<>();
            ways.forEach(way -> coordinates.addAll(Arrays.asList(way.getPoints().getCoordinates())));
            sort(coordinates);

            for (Coordinate coordinate : coordinates) {
                String data = "{\"board\": " + transportId
                        + ", \"latitude\": " + coordinate.y
                        + ", \"longitude\": " + coordinate.x
                        + "}";
                mqttService.simulate("raw/" + routeId, data);
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void simulatePassengers(String routeId) {
        Route route = routeService.findById(routeId).orElse(null);
        List<Transport> transports = (List<Transport>) transportService.findAllByRoute(route);

        for (Transport transport : transports) {
            int nrOfPeople = new Random().nextInt(2) == 2 ? 51 : 46;
            for (int i = 0; i < nrOfPeople; i++) {
                Ticket ticket = new Ticket();
                ticket.setTransport(transport);
                ticket.setCreationTime(LocalDateTime.now());
                ticketService.save(ticket);
            }
        }
    }
}
