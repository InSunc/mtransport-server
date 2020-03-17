package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.models.dto.RouteDTO;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;
import utm.ptm.mtransportserver.services.RouteService;
import utm.ptm.mtransportserver.services.RouteWayService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @GetMapping
    public Route getRoutes() {
        return routeService.getRoute("t2");
    }

    @GetMapping("/{route}")
    public List<Way> getRoute(@PathVariable(name = "route") String routeName) {
        RouteDTO routeDTO = new RouteDTO();

        Route route = routeService.getRoute(routeName);

        return routeService.getWays(route);
    }
}
