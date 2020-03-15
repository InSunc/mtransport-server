package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.RouteWay;
import utm.ptm.mtransportserver.models.dto.RouteWayDTO;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteWayService {
    @Autowired
    private RouteWayRepository routeWayRepository;

    public List<RouteWayDTO> get(long id) {

        return null;
    }

    public List<RouteWayDTO> getAll() {
        List<RouteWay> dbResult = routeWayRepository.findAll();
        List<RouteWayDTO> result = new ArrayList<>();

        for (RouteWay routeWay : dbResult) {
            result.add(new RouteWayDTO(routeWay));
        }

        return result;
    }
}
