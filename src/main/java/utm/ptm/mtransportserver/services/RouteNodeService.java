package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.RouteNode;
import utm.ptm.mtransportserver.models.dto.RouteNodeDTO;
import utm.ptm.mtransportserver.repositories.RouteNodesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteNodeService {
    @Autowired
    private RouteNodesRepository routeNodesRepository;

    public List<RouteNodeDTO> get(long id) {

        return null;
    }

    public List<RouteNodeDTO> getAll() {
        List<RouteNode> dbResult = routeNodesRepository.findAll();
        List<RouteNodeDTO> result = new ArrayList<>();

        for (RouteNode routeNode : dbResult) {
            result.add(new RouteNodeDTO(routeNode));
        }

        return result;
    }
}
