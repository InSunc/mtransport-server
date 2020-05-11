package utm.ptm.mtransportserver.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.repositories.*;
import utm.ptm.mtransportserver.services.RouteService;
import utm.ptm.mtransportserver.services.StopService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


/*
    TODO: implement readRouteDataFromFile function
 */

@Component
public class OverpassDataParser {
    public enum RouteDataType { WAYS, STOPS }
    public static final String serverURL = "https://www.overpass-api.de/api/interpreter?data=";

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteWayRepository routeWayRepository;

    @Autowired
    private StopService stopService;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private WayRepository wayRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

    public void getRouteDataFromServer(String... routeNames) throws Exception {
        for (String routeName : routeNames) {
            getRouteDataFromServer(routeName, OverpassDataParser.RouteDataType.WAYS);
			getRouteDataFromServer(routeName, OverpassDataParser.RouteDataType.STOPS);
		}
    }

    public void getRouteDataFromServer(String routeName, RouteDataType routeData) throws Exception {
        String urlString = serverURL;

        routeName = routeName.toUpperCase(); // because it's case sensitive

        Route route = new Route(routeName, 2f);
        route = routeRepository.save(route);

        routeName += ":"; // end of the name

        switch (routeData) {
            case WAYS: {
                urlString += new String("[out:json][timeout:25];%20relation(46.9650,%2028.7763,%2047.0704,%2028.9277)[type=route][name~\""
                        + routeName
                        + "\"];%20way(r);%20out%20geom;");
                break;
            }

            case STOPS: {
                urlString += new String("[out:json];relation(46.9650,%2028.7763,%2047.0704,%2028.9277)[name~%22"
                        + routeName
                        + "%22];node(r)[public_transport=platform];out%20geom;");


                break;
            }

            default: throw new Exception("Unknown RouteDataType");
        }

        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        JsonReader jsonReader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()));
        urlConnection.connect();

        Gson gson = new Gson();
        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);

        switch (routeData) {
            case WAYS: {
                parseRouteWays(route, result);
                break;
            }

            case STOPS: {
                parseRouteStops(route, result);
                break;
            }
        }
    }

    private void parseRouteWays(Route route, OverpassResultDTO result) {
        GeometryFactory geometryFactory = new GeometryFactory();
        for (ElementDTO element : result.elements) {
            Way way = new Way(element.id, element.tags.getName());

            CoordinateList coordinates = new CoordinateList();

            for (int i = 0; i < element.nodes.length; i++) {
                Point point = geometryFactory.createPoint(new Coordinate(element.geometry[i].lon, element.geometry[i].lat));

                coordinates.add(point.getCoordinate());
            }

            String oneway = element.tags.oneway;
            boolean isBidir = false;
            if (oneway == null || oneway.equals("no")) {
                isBidir = true;
            }
            way.setBidirectional(isBidir);

            CoordinateSequence coordinateSequence = new CoordinateArraySequence(coordinates.toCoordinateArray());
            LineString lineString = new LineString(coordinateSequence, geometryFactory);
            way.setPoints(lineString);
            way = wayRepository.save(way);

            RouteWay routeWay = new RouteWay();
            routeWay.setRoute(route);
            routeWay.setWay(way);
            routeWayRepository.save(routeWay);
        }
    }


    private void parseRouteStops(Route route, OverpassResultDTO result) {
        GeometryFactory geometryFactory = new GeometryFactory();
        for (ElementDTO element : result.elements) {
//            System.out.println(element.tags.getName());
            Point point = geometryFactory.createPoint(new Coordinate(element.lon, element.lat));
            Stop stop = stopService.findById(element.id).orElse(new Stop());
            stop.setId(element.id);
            Way way = wayRepository.findNearest(point).orElse(null);
            stop.setWay(way);
            stop.setLocation(point);
            stop.setName(element.tags.getName());
            stop = stopService.save(stop);

            int side = 0;
            Point startPoint = null;
            Point endPoint = null;
            if (way.getPointsOrder() == 1) {
                startPoint = way.getPoints().getStartPoint();
                endPoint = way.getPoints().getEndPoint();
            } else if (way.getPointsOrder() == -1) {
                endPoint = way.getPoints().getStartPoint();
                startPoint = way.getPoints().getEndPoint();
            } else if (way.getPointsOrder() == 0) {
                System.err.println(" !!! IN OVERPASS PARSER");
                startPoint = way.getPoints().getStartPoint();
                endPoint = way.getPoints().getEndPoint();
            }
            double det = (endPoint.getX() - startPoint.getX())*(stop.getLocation().getY() - startPoint.getY())
                    - (stop.getLocation().getX() - startPoint.getX())*(endPoint.getY() - startPoint.getY());

            if (det > 0) side = 1;
            if (det < 0) side = -1;
            if (det == 0) side = 0;

            if (way.isBidirectional()) side *= -1;

            stop.setSide(side);
            stop = stopService.save(stop);

            RouteStop routeStop = new RouteStop();
            routeStop.setStop(stop);
            routeStop.setRoute(route);

            routeService.save(routeStop);
        }
    }

    /*
        Result of overpass-turbo query:
            [out:json][timeout:25];
            relation(46.9650, 28.7763, 47.0704, 28.9277)[name~"T2:"];
            node(r)[public_transport=platform];
            out geom;
     */
//    public void getRouteStopsFromJson(String routeName, String filename) throws IOException {
//        File file = new File(filename);
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new FileReader(file));
//        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);
//
//        Route route = routeRepository.save(new Route(routeName));
//        GeometryFactory geometryFactory = new GeometryFactory();
//
//        for (ElementDTO element : result.elements) {
//            Point point = geometryFactory.createPoint(new Coordinate(element.lon, element.lat));
//            Stop stop = new Stop();
//            Node node = new Node(element.id, point);
//
//            stop.setStopNode(node);
//            stop.setName(element.tags.getName());
//            node.setStopNode(stop);
//
//            nodeService.save(node);
//        }
//    }


    /*
        Result of overpass-turbo query:
            [out:json][timeout:25];
            relation(46.9650, 28.7763, 47.0704, 28.9277)[type=route][name~"T2:"];
            way(r);
            out geom;
     */
//    public void getRouteWaysFromJson(String routeName, String filename) throws FileNotFoundException {
//        File file = new File(filename);
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new FileReader(file));
//        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);
//
//        Route route = routeRepository.save(new Route(routeName));
//
//        for (ElementDTO element : result.elements) {
//            Way way = new Way(element.id, element.tags.getName());
//            way = wayRepository.save(way);
//
//            for (int i = 0; i < element.nodes.length; i++) {
//                GeometryFactory geometryFactory = new GeometryFactory();
//                Point point = geometryFactory.createPoint(new Coordinate(element.geometry[i].lon, element.geometry[i].lat));
//                Node node = new Node(element.nodes[i], point);
//                node = nodeService.save(node);
//
//                wayNodeRepository.save(wayNode);
//            }
//
//            RouteWay routeWay = new RouteWay();
//            routeWay.setRoute(route);
//            routeWay.setWay(way);
//            routeWayRepository.save(routeWay);
//        }
//    }
}


class OverpassResultDTO {
    double version;
    String generator;
    ElementDTO[] elements;
}

class ElementDTO {
    long id;
    String type;
    TagsDTO tags;
    double lat;
    double lon;
    long[] nodes;
    GeometryDTO[] geometry;
}

class GeometryDTO {
    double lat;
    double lon;
}

class TagsDTO {
    String name;
    @SerializedName("name:ru")
    String nameRU;
    String public_transport;
    String oneway;
    String noexit;

    String getName() {
        if (name != null)
            return name;

        if (nameRU != null)
            return nameRU;

        return "unnamed";
    }
}