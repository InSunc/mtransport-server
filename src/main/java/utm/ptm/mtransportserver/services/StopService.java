package utm.ptm.mtransportserver.services;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteStop;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.repositories.NodeRepository;
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
    private NodeRepository nodeRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;



    public Stop getNearest(Point point) {
        return stopRepository.findNearest(point);
    }

    public Stop save(Stop stop) {
        return stopRepository.save(stop);
    }

    public Optional<Stop> findById(Stop stop) {
        return stopRepository.findById(stop.getStopNode().getId());
    }

    public Optional<Stop> findById(Long id) {
        return stopRepository.findById(id);
    }

    public List<Stop> getByRoute(Route route) {
        List<RouteStop> routeStops = routeStopRepository.findAllByRoute(route);
        List<Stop> stops = new ArrayList<>();
        routeStops.forEach(routeStop -> stops.add(routeStop.getStop()));

        return stops;
    }
}
