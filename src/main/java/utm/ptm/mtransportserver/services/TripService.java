package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;

import java.util.*;

@Service
public class TripService {
    @Autowired
    private RouteService routeService;

    @Autowired
    private StopService stopService;


    public HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> findTrips(Stop originStop, Stop destinationStop) {

        LinkedList<Stop> vStops = new LinkedList<>();
        LinkedList<Route> vRoutes = new LinkedList<>();

        Map<Route, LinkedHashSet<Stop>> routeStops = new HashMap<>();

        List<Route> routes = routeService.findAll();
        for (Route route : routes) {
            List<Stop> stopsList = stopService.findByRoute(route);
            LinkedHashSet<Stop> stops = new LinkedHashSet<>(stopsList);
            routeStops.put(route, stops);
        }

        HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result = new HashMap<>();
        vStops.addLast(originStop);
        search(routeStops, vStops, vRoutes, destinationStop, result);

        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
            for (Route route : entry.getKey()) {
                System.out.print(route.getId() + " - ");
            }
            System.out.println();
            for (Stop stop : entry.getValue()) {
                System.out.println("\t" + stop.getId() + " -> " + stop.getName());
            }
            System.out.println("=======================");
        }

        return result;
    }


    private void search(Map<Route, LinkedHashSet<Stop>> routeStops, LinkedList<Stop> vStops,
                       LinkedList<Route> vRoutes, Stop destination,
                       HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result) {

        for (Map.Entry<Route, LinkedHashSet<Stop>> e : routeStops.entrySet()) {

            Route route = e.getKey();
            LinkedHashSet<Stop> stops = e.getValue();

            if (!vRoutes.contains(route)) {
                if (stops.contains(vStops.getLast())) {
                    vRoutes.addLast(route);

                    for (Stop stop : stops) {
                        if (stop.equals(destination)) {
                            // return results
                            vStops.addLast(stop);
                            result.put(new LinkedHashSet<>(vRoutes), new LinkedHashSet<>(vStops));
                            vStops.removeLast();
                            break;
                        }

                        if (!vStops.contains(stop)) {
                            vStops.addLast(stop);
                            search(routeStops, vStops, vRoutes, destination, result);
                            vStops.removeLast();
                        }
                    }

                    vRoutes.removeLast();
                }
            }
        }
    }
}

