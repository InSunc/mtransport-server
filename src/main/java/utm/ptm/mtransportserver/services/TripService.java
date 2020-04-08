package utm.ptm.mtransportserver.services;

import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.minidev.json.JSONUtil;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geomgraph.PlanarGraph;
import org.locationtech.jts.operation.linemerge.LineSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.image.ImageWatched;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TripService {
    @Autowired
    private RouteService routeService;

    @Autowired
    private StopService stopService;

    @Autowired
    private WayService wayService;


    public HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> findTrips(Stop originStop, Stop destinationStop) {

        LinkedList<Stop> vStops = new LinkedList<>();
        LinkedList<Route> vRoutes = new LinkedList<>();
        Map<Route, LinkedHashSet<Stop>> routeStops = preProcess(originStop, destinationStop);

        HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result = new HashMap<>();
        vStops.addLast(originStop);
        search(routeStops, vStops, vRoutes, destinationStop, result);
        postProcess(result);

        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
            for (Route route : entry.getKey()) {
                System.out.print(route.getId() + " - ");
            }

            System.out.println();
            for (Stop stop : entry.getValue()) {
                System.out.println("\t" + stop.getId() + " -> " + stop.getName());
            }
            System.out.println("------------------------");

            System.out.println("=======================");
        }
        findTripWays(result);


        return result;
    }

    public void findTripWays(HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result) {
        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {

            List<Stop> stops = new ArrayList<>(entry.getValue());
            List<Route> routes = new ArrayList<>(entry.getKey());
            List<Way> stopWays = new ArrayList<>();
            for (Stop stop : stops) {
                stopWays.add(stop.getWay());
            }

            Way originWay = stops.get(0).getWay();
            Way destinationWay = stops.get(stops.size() - 1).getWay();
            Map<Route, List<Way>> routeWays = wayService.getRouteWays(routes);
            Set<Way> ways = new HashSet<>();
            for (Map.Entry<Route, List<Way>> routeWaysEntry : routeWays.entrySet()) {
                ways.addAll(routeWaysEntry.getValue());
            }

//            if (originWay.getPointsOrder() == 0) {
//                if (destinationWay.getPointsOrder() == 0) {
//                    predicate = way -> way.getPointsOrder() == way.getPointsOrder();
//                    System.out.println("-------- origin side");
//                } else {
//                    predicate = way -> way.getPointsOrder() == destinationWay.getPointsOrder() || way.getPointsOrder() == 0;
//                    System.out.println("-------- destination order");
//                }
//            } else {
//                predicate = way -> way.getPointsOrder() == originWay.getPointsOrder() || way.getPointsOrder() == 0;
//                System.out.println("-------- origin order");
//            }
//            ways = ways.stream().filter(predicate).collect(Collectors.toSet());

            LinkedList<Way> vWays = new LinkedList<>();
            LinkedList<Point> vPoints = new LinkedList<>();
            Map<Point, List<Way>> wayPoints = new HashMap<>();
            for (Way way : ways) {
                Point start = way.getPoints().getStartPoint();
                Point end = way.getPoints().getEndPoint();
                wayPoints.put(start, new ArrayList<>());
                wayPoints.put(end, new ArrayList<>());
            }
            for (Way way : ways) {
                Point start = way.getPoints().getStartPoint();
                Point end = way.getPoints().getEndPoint();
                wayPoints.get(start).add(way);
                wayPoints.get(end).add(way);
            }


//            for (Map.Entry<Point, List<Way>> pw : wayPoints.entrySet()) {
//                System.out.println(pw.getKey() + ":");
//                pw.getValue().forEach(x -> System.out.println("\t" + x.getId() + "\t" + x.getName() + "\t"));
//                System.out.println();
//            }


            LinkedHashSet<LinkedHashSet<Way>> paths = new LinkedHashSet<>();
            vWays.add(originWay);
//        vWays.add(originWay);
            search(wayPoints, vPoints, vWays, destinationWay, stopWays, paths);

            List<LinkedHashSet<Way>> pathList = new ArrayList<>();
            for (LinkedHashSet<Way> path : paths) {
                pathList.add(path);
            }

            System.out.println(" !!!!!!!!!!!!!! " + pathList.size());

            int maxMark = rate(pathList.get(0), originWay, destinationWay);
            int index = 0;
            for (int i = 1; i < pathList.size(); i++) {
                int mark = rate(pathList.get(i), originWay, destinationWay);
                if (mark > maxMark) {
                    maxMark = mark;
                    index = i;
                }
            }

            for (Way way : pathList.get(index)) {
                System.out.println(way.getId() + " --> " + way.getName());
            }

//            for (LinkedHashSet<Way> path : paths) {
//                path.forEach(x -> System.out.println(x.getId() + " --> " + x.getName()));
//                System.out.println("`````````````````````````````````````````````````````````");
//            }
        }
    }

    private int rate(Iterable<Way> ways, Way origin, Way destination) {
        int result = 0;
        for (Way way : ways) {
            if (!origin.isBidirectional()) {
                if (!destination.isBidirectional()) {
                    if (way.getPointsOrder() == origin.getPointsOrder() || way.getPointsOrder() == destination.getPointsOrder())
                        result++;
                }
            }
        }

        return result;
    }


    private Map<Route, LinkedHashSet<Stop>> preProcess(Stop originStop, Stop destinationStop) {
        Map<Route, LinkedHashSet<Stop>> routeStops = new HashMap<>();

        List<Route> routes = routeService.findAll();
        for (Route route : routes) {
            List<Stop> stopsList = stopService.findByRoute(route);

            Predicate<Stop> predicate;
            int pointsOrder = originStop.getWay().getPointsOrder();
            if (pointsOrder == 0) { // if the way is bidirectional, direction will be expressed by the stop side
                pointsOrder = destinationStop.getWay().getPointsOrder();
                if (pointsOrder == 0) {
                    predicate = stop -> stop.getSide() == originStop.getSide();
//                    System.out.println("filter by origin stop side");
                } else {
                    final int order = pointsOrder;
                    predicate = stop -> stop.getWay().getPointsOrder() == order || stop.getWay().getPointsOrder() == 0;
//                    System.out.println("filter by destination order");
                }
            } else {
                final int order = pointsOrder;
//                System.out.println("filter by origin order");
                predicate = stop -> stop.getWay().getPointsOrder() == order;
            }
            stopsList = stopsList.stream().filter(predicate).collect(Collectors.toList());

            LinkedHashSet<Stop> stops = new LinkedHashSet<>(stopsList);
//            System.out.println(" >>> origin stop " + stopsList.contains(originStop));
//            System.out.println(" >>> destination stop " + stopsList.contains(destinationStop));
//            System.out.println("-------------------------------------------");
            routeStops.put(route, stops);
        }

        return routeStops;
    }


    private void postProcess(HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result) {
        List<Integer> sizes = new ArrayList<>();
        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
            sizes.add(entry.getKey().size());
        }
        int max = Collections.max(sizes);
        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
            if (entry.getKey().size() == max) result.remove(entry.getKey());
        }
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
                        if (!vStops.contains(stop)) {
                            vStops.addLast(stop);
                            if (stop.equals(destination)) {
                                // return results
                                result.put(new LinkedHashSet<>(vRoutes), new LinkedHashSet<>(vStops));
                            } else {
                                search(routeStops, vStops, vRoutes, destination, result);
                            }
                            vStops.removeLast();
                        }
                    }

                    vRoutes.removeLast();
                }
            }
        }
    }


    private void search(Map<Point, List<Way>> wayPoints, LinkedList<Point> vPoints,
                        LinkedList<Way> vWays, Way destination, List<Way> stopWays, LinkedHashSet<LinkedHashSet<Way>> paths) {

        for (Map.Entry<Point, List<Way>> e : wayPoints.entrySet()) {

            Point point = e.getKey();
            List<Way> ways = e.getValue();
            if (!vPoints.contains(point)) {
                if (ways.contains(vWays.getLast())) {
                    vPoints.addLast(vWays.getLast().getPoints().getStartPoint());
                    vPoints.addLast(vWays.getLast().getPoints().getEndPoint());

                    for (Way way : ways) {
                        if (!vWays.contains(way)) {
                            vWays.addLast(way);
                            if (way.equals(destination)) {
                                // return results
                                LinkedHashSet<Way> waySet = new LinkedHashSet<>(vWays);
                                if (waySet.containsAll(stopWays)) {
                                    paths.add(waySet);
                                }
                            } else {
                                search(wayPoints, vPoints, vWays, destination, stopWays, paths);
                            }
                            vWays.removeLast();
                        }
                    }

                    vPoints.removeLast();
                    vPoints.removeLast();
                }
            }
        }
    }
}
