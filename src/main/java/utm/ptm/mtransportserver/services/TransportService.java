package utm.ptm.mtransportserver.services;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.operation.linemerge.LineSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.repositories.TicketRepository;
import utm.ptm.mtransportserver.repositories.TransportArrivalRepository;
import utm.ptm.mtransportserver.repositories.TransportRepository;

import java.util.*;

@Service
public class TransportService {

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private WayService wayService;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransportArrivalRepository transportArrivalRepository;

    @Autowired
    private TicketRepository ticketRepository;


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

    public int getNrOfProple(long transportId) {
        List<Ticket> tickets = ticketRepository.getNumberOfPeople(transportId);
        return tickets.size();
    }

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Transport save(Transport transport) {
        return transportRepository.save(transport);
    }


    public Transport findById(Long id) {
        return transportRepository.findById(id).get();
    }


    public TransportArrival save(TransportArrival transportArrival) {
        return transportArrivalRepository.save(transportArrival);
    }

    private List<List<Coordinate>> sortR(List<List<Coordinate>> rawWays) {
        rawWays.forEach(way -> Collections.sort(way, new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate a, Coordinate b) {
                return a.compareTo(b);
            }
        }));

        Collections.sort(rawWays, new Comparator<List<Coordinate>>() {
            @Override
            public int compare(List<Coordinate> a, List<Coordinate> b) {
                Coordinate aStart = a.get(0);
                Coordinate bStart = b.get(0);
                return aStart.compareTo(bStart);
            }
        });
        List<List<Coordinate>> sorted = new ArrayList<>();
        for (int i = 0; i < rawWays.size(); i++) {
            List<Coordinate> way = rawWays.get(i);
            Coordinate start = way.get(0);
            Coordinate end = way.get(way.size() - 1);

            int found = -1;
            for (int j = i + 1; j < rawWays.size(); j++) {
                List<Coordinate> nextWay = rawWays.get(i);
                Coordinate nextStart = way.get(0);
                Coordinate nextEnd = way.get(way.size() - 1);
                if (nextStart.compareTo(start) == 0) {
                    found = j;
                    break;
                }
            }
            if (found > -1) {
                sorted.add(rawWays.get(i));
                sorted.add(rawWays.get(found));
            }
        }
        return sorted;
    }


    public void simulate(String routeId) {
        Route route = routeService.findById(routeId).get();
        Transport transport = new Transport();
        transport.setRoute(route);
        int transportId = new Random().nextInt();
        transport.setId(transportId);
        transport = transportRepository.save(transport);

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
                mqttService.publish(routeId, data);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
//        List<List<Coordinate>> rawWays = new ArrayList<>();
//        ways.forEach(way -> rawWays.add(Arrays.asList(way.getPoints().getCoordinates())));
//        List<List<Coordinate>> sorted = sortR(rawWays);

//        for (List<Coordinate> rawWay : sorted) {
//            for (Coordinate coordinate : rawWay) {
//                String data = "{\"board\": " + transportId
//                        + ", \"latitude\": " + coordinate.y
//                        + ", \"longitude\": " + coordinate.x
//                        + "}";
//                mqttService.publish(routeId, data);
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }

            List<Coordinate> coordinates = new ArrayList<>();
            ways.forEach(way -> coordinates.addAll(Arrays.asList(way.getPoints().getCoordinates())));
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
}
