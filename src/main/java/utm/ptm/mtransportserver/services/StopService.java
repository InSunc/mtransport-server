package utm.ptm.mtransportserver.services;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.repositories.RouteStopRepository;
import utm.ptm.mtransportserver.repositories.StopRepository;
import utm.ptm.mtransportserver.repositories.TransportArrivalRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class StopService {
    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private TransportArrivalRepository transportArrivalRepository;

    public Stop findNearest(Point point) {
        Stop stop = stopRepository.findNearest(point).orElse(null);
        if (stop == null) {
            throw new NullPointerException();
        } else {
            return stop;
        }
    }

    public Stop findNearest(Point point, int side) {
        Stop stop = stopRepository.findNearest(point, side).orElse(null);
        if (stop == null) {
            throw new NullPointerException();
        } else {
            return stop;
        }
    }

    public int getStopsNr(Iterable<Way> ways, int side) {
        return stopRepository.findAllByWay(ways, side).size();
    }


    public Optional<Stop> findByDistance(Point point, int maxDistance) {
        return stopRepository.findByDistance(point, maxDistance);
    }

    public Optional<Stop> findByDistance(Point point, String routeId, int maxDistance) {
        return stopRepository.findByDistance(point, routeId, maxDistance);
    }

    public Stop save(Stop stop) {
        return stopRepository.save(stop);
    }

    public Optional<Stop> findById(Long id) {
        return stopRepository.findById(id);
    }

    public List<Stop> findByRoute(Route route) {
        List<RouteStop> routeStops = routeStopRepository.findAllByRoute(route);
        List<Stop> stops = new ArrayList<>();
        routeStops.forEach(routeStop -> stops.add(routeStop.getStop()));

        return stops;
    }

    public List<TransportArrival> getTransportArrivals(Long stopId, String routeId) {
        Stop stop = stopRepository.findById(stopId).get();
        Route route = routeService.findById(routeId).get();

        List<TransportArrival> transportArrivals = transportArrivalRepository.findAllByStop(stop);
        Predicate<TransportArrival> predicate = ta -> ta.getTransport().getRoute().equals(route);
        transportArrivals = transportArrivals.stream().filter(predicate).collect(Collectors.toList());

        return transportArrivals;
    }

}
