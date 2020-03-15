package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utm.ptm.mtransportserver.models.dto.RouteWayDTO;
import utm.ptm.mtransportserver.repositories.RouteWayRepository;
import utm.ptm.mtransportserver.services.RouteWayService;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import java.io.FileNotFoundException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    RouteWayRepository routeWayRepository;

    @Autowired
    private RouteWayService routeWayService;

    @Autowired
    private OverpassDataParser overpassDataParser;

    @GetMapping
    public String getRoutes() {
        try {
            overpassDataParser.getRouteWaysFromJson("t2","t2/t2-ways.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        List<RouteWayDTO> path = routeWayService.getAll();
//
        return "Success";
    }
}
