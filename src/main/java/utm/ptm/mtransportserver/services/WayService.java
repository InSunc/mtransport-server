package utm.ptm.mtransportserver.services;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteWay;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.repositories.RouteRepository;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;
import utm.ptm.mtransportserver.repositories.WayRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WayService {

    @Autowired
    private RouteWayRepository routeWayRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private WayRepository wayRepository;



    public List<Way> getRouteWays(Route route) {
        route = routeRepository.findById(route.getId()).get();
        List<RouteWay> routeWays = routeWayRepository.findAllByRoute(route);
        List<Way> ways = new ArrayList<>();
        routeWays.forEach(x -> ways.add(x.getWay()));

        return ways;
    }


    public Optional<Way> findNearest(String routeId, Point stopLocation) {
        return wayRepository.findNearest(routeId, stopLocation);
    }
}
