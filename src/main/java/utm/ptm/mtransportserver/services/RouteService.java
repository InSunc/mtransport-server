package utm.ptm.mtransportserver.services;


import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteStop;
import utm.ptm.mtransportserver.models.db.RouteWay;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.models.dto.WayDTO;
import utm.ptm.mtransportserver.repositories.RouteRepository;
import utm.ptm.mtransportserver.repositories.RouteStopRepository;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private RouteWayRepository routeWayRepository;

    public List<Route> getAll() {
        return routeRepository.findAll();
    }

    public Optional<Route> getRoute(String routeId) {
        return routeRepository.findById(routeId);
    }

    public RouteStop save(RouteStop routeStop) {
        return routeStopRepository.save(routeStop);
    }

}
