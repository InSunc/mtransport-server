package utm.ptm.mtransportserver.services;


import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
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

    public float getTripTime(LinkedHashSet<Way> ways, int side) {
        double distance = 0;
        for (Way way : ways) {
            distance += way.getPoints().getLength();
        }

        distance = distance/180 * Math.PI * 6371;

        System.out.println(" * distance = " + distance);

        int stops = stopService.getStopsNr(ways, side);

        System.out.println(" * stops = " + stops);

        double v = 45/60f; // km/minute

        double c = 2; // minutes

        return (float)(distance/v + c * stops);
    }

    public double rate(List<Stop> stops) {
        double distance = 0;
        for (int i = 0; i < stops.size() - 1; i++) {
            distance = stops.get(i).getLocation().distance(stops.get(i + 1).getLocation());
        }

        return distance;
    }


    public float getTripCost(Iterable<Route> routes) {
        float price = 0;
        for (Route route : routes) {
            price += route.getPrice();
        }

        return price;
    }

    public Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> getBestResult(HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> trips) {
        if (trips.size() > 1) {
//        List<Double> rates = new ArrayList<>();
//        List<Float> costs = new ArrayList<>();
            List<Integer> sizes = new ArrayList<>();
            for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : trips.entrySet()) {
//            rates.add(rate(new ArrayList<>(entry.getValue())));
//            costs.add(getTripCost(new ArrayList<>(entry.getKey())));
                sizes.add(entry.getKey().size());
            }

            int sizeIndex = sizes.indexOf(Collections.min(sizes));
//        int rateIndex = rates.indexOf(Collections.min(rates));
//        int costIndex = costs.indexOf(Collections.min(costs));

            Iterator<Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>>> it = trips.entrySet().iterator();

            for (int i = 0; i < sizeIndex; i++) {
                it.next();
            }

            return it.next();
        }
        return trips.entrySet().iterator().next();
    }


    public HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> findTripStops(Stop originStop, Stop destinationStop) {

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

        return result;
    }

    @Cacheable("tripWays")
    public ConcurrentHashMap<LinkedHashSet<Route>, LinkedHashSet<Way>> findTripWays(HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> trips) {
        ConcurrentHashMap<LinkedHashSet<Route>, LinkedHashSet<Way>> results = new ConcurrentHashMap<>();

        Thread[] threads = new Thread[trips.size()];
        int i = 0;

        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> trip : trips.entrySet()) {
            Map<Route, List<Way>> routeWays = wayService.getRouteWays(trip.getKey());
            Set<Way> waySet = new HashSet<>();
            for (Map.Entry<Route, List<Way>> routeWaysEntry : routeWays.entrySet()) {
                waySet.addAll(routeWaysEntry.getValue());
            }
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    LinkedHashSet<Way> waysPath = findTripWays(waySet, new ArrayList<>(trip.getValue()));
                    results.put(trip.getKey(), waysPath);
                }
            });
            threads[i++].start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }

    // TODO: Delete it or idk
//    public HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> findTrips(Stop originStop, Stop destinationStop) {
//
//        LinkedList<Stop> vStops = new LinkedList<>();
//        LinkedList<Route> vRoutes = new LinkedList<>();
//        Map<Route, LinkedHashSet<Stop>> routeStops = preProcess(originStop, destinationStop);
//
//        HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result = new HashMap<>();
//        vStops.addLast(originStop);
//        search(routeStops, vStops, vRoutes, destinationStop, result);
//        postProcess(result);
//
//        for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
//            for (Route route : entry.getKey()) {
//                System.out.print(route.getId() + " - ");
//            }
//
//            System.out.println();
//            for (Stop stop : entry.getValue()) {
//                System.out.println("\t" + stop.getId() + " -> " + stop.getName());
//            }
//            System.out.println("------------------------");
//            findTripWays(new ArrayList<>(entry.getKey()), new ArrayList<>(entry.getValue()));
//            System.out.println("=======================");
//        }
//
//        return result;
//    }

    public LinkedHashSet<Way> findTripWays(Set<Way> ways, List<Stop> stops) {
        List<Way> stopWays = new ArrayList<>();
        for (Stop stop : stops) {
            stopWays.add(stop.getWay());
        }

        Way originWay = stops.get(0).getWay();
        Way destinationWay = stops.get(stops.size() - 1).getWay();
//        Map<Route, List<Way>> routeWays = wayService.getRouteWays(routes);


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

        LinkedHashSet<LinkedHashSet<Way>> paths = new LinkedHashSet<>();
        vWays.add(originWay);
        search(wayPoints, vPoints, vWays, destinationWay, stopWays, paths);

        List<LinkedHashSet<Way>> pathList = new ArrayList<>();
        for (LinkedHashSet<Way> path : paths) {
            pathList.add(path);
        }

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


        return pathList.get(index);
    }

    private int rate(Iterable<Way> ways, Way origin, Way destination) {
        int result = 0;
        for (Way way : ways) {
            if ((origin.isBidirectional() == false && way.getPointsOrder() == origin.getPointsOrder())
                    || (destination.isBidirectional() == false && way.getPointsOrder() == destination.getPointsOrder())) {
                result += 2;
            } else {
                result--;
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
            if (originStop.getWay().isBidirectional()) {
                if (destinationStop.getWay().isBidirectional()) {
                    predicate = stop -> stop.getSide() == originStop.getSide();
                } else {
                    int pointsOrder = destinationStop.getWay().getPointsOrder();
                    predicate = stop -> stop.getWay().getPointsOrder() == pointsOrder || stop.getWay().isBidirectional();
                }
            } else {
                int pointsOrder = originStop.getWay().getPointsOrder();
                predicate = stop -> stop.getWay().getPointsOrder() == pointsOrder || stop.getWay().isBidirectional();
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


    private void postProcess(HashMap<LinkedHashSet<Route>, LinkedHashSet<Stop>> result) throws NoSuchElementException {
        if (result.entrySet().size() > 1) {
            List<Integer> sizes = new ArrayList<>();
            for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
                sizes.add(entry.getKey().size());
            }
            int max = Collections.max(sizes);
            for (Map.Entry<LinkedHashSet<Route>, LinkedHashSet<Stop>> entry : result.entrySet()) {
                if (entry.getKey().size() == max) result.remove(entry.getKey());
            }
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
