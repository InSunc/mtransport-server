package utm.ptm.mtransportserver.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.repositories.*;
import utm.ptm.mtransportserver.services.NodeService;
import utm.ptm.mtransportserver.services.StopService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class OverpassDataParser {
    @Autowired
    private NodeService nodeService;

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
    private WayNodeRepository wayNodeRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

    /*
        Result of overpass-turbo query:
            [out:json][timeout:25];
            relation(46.9650, 28.7763, 47.0704, 28.9277)[name~"T2:"];
            node(r);
            out geom;
     */
    public void getRouteStopsFromJson(String routeName, String filename) throws IOException {
        File file = new File(filename);

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new FileReader(file));
        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);

        Route route = routeRepository.save(new Route(routeName));
        List<RouteStop> routeStops = new ArrayList<>();
        List<Stop> stops = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();

        for (ElementDTO element : result.elements) {
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(element.lon, element.lat));
//            Node node = nodeService.save(new Node(element.id, point));
            Node node = new Node(element.id, point);


            if (element.tags.public_transport.equals("platform")) {
                Stop stop = new Stop();
                stop.setName(element.tags.getName());
                stop.setStopNode(node);
                stops.add(stop);
            } else if (element.tags.public_transport.equals("stop_position")) {
                nodes.add(node);
            }
        }

        Collections.sort(stops);

        stops = stopService.saveAll(stops);

        Collections.sort(nodes);

        for (int i = 0; i < stops.size(); i++) {
            stops.get(i).setRouteNode(nodes.get(i));
        }

        nodeService.saveAll(nodes);
        stops = stopService.saveAll(stops);


        stops.forEach(x -> routeStops.add(new RouteStop(route, x)));
        routeStopRepository.saveAll(routeStops);

//        for (Stop stop : stops) {
//            Node nearestNode = nodeService.findNearest(stop.getStopNode());
//            stop.setRouteNode(nearestNode);
//            stop = stopService.save(stop);
//        }

//        System.out.println(" >>>>> BEFORE" + routeName + "  " + stops.size());
//        stops = stopService.saveAll(stops);

//        System.out.println(" >>>>> AFTER " + routeName + "  " + stops.size());
    }


    /*
        Result of overpass-turbo query:
            [out:json][timeout:25];
            relation(46.9650, 28.7763, 47.0704, 28.9277)[type=route][name~"T2:"];
            way(r);
            out geom;
     */
    public void getRouteWaysFromJson(String routeName, String filename) throws FileNotFoundException {
        File file = new File(filename);

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new FileReader(file));
        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);

        Route route = routeRepository.save(new Route(routeName));

        for (ElementDTO element : result.elements) {
            Way way = new Way(element.id, element.tags.getName());
            way = wayRepository.save(way);

            for (int i = 0; i < element.nodes.length; i++) {
                GeometryFactory geometryFactory = new GeometryFactory();
                Point point = geometryFactory.createPoint(new Coordinate(element.geometry[i].lon, element.geometry[i].lat));
                Node node = new Node(element.nodes[i], point);
                node = nodeService.save(node);

                WayNode wayNode = new WayNode();
                wayNode.setWay(way);
                wayNode.setNode(node);

                wayNodeRepository.save(wayNode);
            }

            RouteWay routeWay = new RouteWay();
            routeWay.setRoute(route);
            routeWay.setWay(way);
            routeWayRepository.save(routeWay);
        }
    }
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

    String getName() {
        if (name != null)
            return name;

        if (nameRU != null)
            return nameRU;

        return "unnamed";
    }
}