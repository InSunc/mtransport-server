package utm.ptm.mtransportserver.services;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteStop;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.models.dto.CoordinateDTO;
import utm.ptm.mtransportserver.models.dto.StopDTO;
import utm.ptm.mtransportserver.repositories.RouteStopRepository;
import utm.ptm.mtransportserver.repositories.StopRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StopService {
    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

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


    public Optional<Stop> findByDistance(Point point, int maxDistance) {
        return stopRepository.findByDistance(point, maxDistance);
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
}
