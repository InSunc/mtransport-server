package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;

@Service
public class RouteWayService {
    @Autowired
    private RouteWayRepository routeWayRepository;


}
