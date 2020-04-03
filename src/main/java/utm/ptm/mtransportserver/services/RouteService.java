package utm.ptm.mtransportserver.services;


import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.models.dto.WayDTO;
import utm.ptm.mtransportserver.repositories.RouteRepository;
import utm.ptm.mtransportserver.repositories.RouteStopRepository;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;

import java.util.*;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private RouteWayRepository routeWayRepository;

    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    public Optional<Route> findById(String routeId) {
        return routeRepository.findById(routeId);
    }

    public RouteStop save(RouteStop routeStop) {
        return routeStopRepository.save(routeStop);
    }

    public List<Route> findAllByWay(Way way){
        List<RouteWay> routeWays =  routeWayRepository.findAllByWay(way);
        List<Route> routes = new ArrayList<>();
        routeWays.forEach(routeWay -> routes.add(routeWay.getRoute()));
        return routes;
    }

    public List<Route> findAllByStop(Stop stop) {
        List<RouteStop> routeStops = routeStopRepository.findAllByStop(stop);
        List<Route> routes = new ArrayList<>();
        routeStops.forEach(routeStop -> routes.add(routeStop.getRoute()));

        return routes;
    }
}
