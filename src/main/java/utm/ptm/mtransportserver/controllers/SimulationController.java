package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.dto.ClientStopArrivalDTO;
import utm.ptm.mtransportserver.services.MqttService;
import utm.ptm.mtransportserver.services.SimulationService;
import utm.ptm.mtransportserver.services.StopService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/simulation")
public class SimulationController {
    @Autowired
    private SimulationService simulationService;
    @Autowired
    private MqttService mqttService;
    @Autowired
    private StopService stopService;


    @GetMapping("/transport/{route}")
    public String simulateRoute(@PathVariable(name = "route") String routeId) {
        simulationService.simulateTransport(routeId);
        return "Simulation ended";
    }

    @GetMapping("/passengers/{route}")
    public String simulatePassengers(@PathVariable(name = "route") String routeId) {
        simulationService.simulatePassengers(routeId);
        return "Passengers added";
    }

    @GetMapping("/stops/{stopId}")
    public String simulateStopsActivity(@PathVariable(name = "stopId") long stopId, @RequestBody ClientStopArrivalDTO req) {
        stopService.save(req);

        return "Client arrived " + req.arrived + " at stopId" + req.stopId;
    }
}
