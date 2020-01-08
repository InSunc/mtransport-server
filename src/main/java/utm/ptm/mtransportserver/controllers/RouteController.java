package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utm.ptm.mtransportserver.models.db.RouteNode;
import utm.ptm.mtransportserver.models.dto.RouteNodeDTO;
import utm.ptm.mtransportserver.repositories.RouteNodesRepository;
import utm.ptm.mtransportserver.services.RouteNodeService;

import java.awt.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    RouteNodesRepository routeNodesRepository;

    @Autowired
    private RouteNodeService routeNodeService;

    @GetMapping
    public List<RouteNodeDTO> getRoutes() {
        List<RouteNodeDTO> path = routeNodeService.getAll();

        return path;
    }
}
