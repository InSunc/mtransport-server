package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.services.MqttService;
import utm.ptm.mtransportserver.services.SimulationService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/simulation")
public class SimulationController {
    @Autowired
    private SimulationService simulationService;
    @Autowired
    private MqttService mqttService;



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
}
