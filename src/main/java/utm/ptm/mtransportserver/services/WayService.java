package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteWay;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class WayService {

    @Autowired
    private RouteWayRepository routeWayRepository;



    public List<Way> getRouteWays(Route route) {
        List<RouteWay> routeWays = routeWayRepository.findAllByRoute(route);
        List<Way> ways = new ArrayList<>();
        routeWays.forEach(x -> ways.add(x.getWay()));

        return ways;
    }
}
