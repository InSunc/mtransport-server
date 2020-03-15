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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


@Component
public class OverpassDataParser {
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteWayRepository routeWayRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private WayRepository wayRepository;

    @Autowired
    private WayNodeRepository wayNodeRepository;

    /*
     * TODO: Filter nodes with empty names
     *       Write a lower level parser
     * Overpass query: node(46.9650, 28.7763, 47.0704, 28.9277)[highway=bus_stop];
     */
    public void getStopsFromJson(String filename) throws IOException {
//        File file = ResourceUtils.getFile("classpath:static/" + filename);
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new FileReader(file));
//        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);
//
//        List<Node> nodes = nodeRepository.findAll();
//
//        for (ElementDTO element : result.elements) {
//            if (element.tags.name == null)
//                continue;
//
//            Node stopNode = new Node();
//            stopNode.setLat(element.lat);
//            stopNode.setLng(element.lon);
//
//            if (!nodes.contains(stopNode)) {
//                stopNode = nodeRepository.save(stopNode);
//            }
//
//            Stop stop = new Stop();
//            stop.setName(element.tags.name);
//            stop.setStopNode(stopNode);
//            stopRepository.save(stop);
//        }
    }

    /*
        Result of overpass-turbo query:
            [out:json][timeout:25];
            relation(46.9650, 28.7763, 47.0704, 28.9277)[type=route][name~"T2:"];
            way(r);
            out geom;
     */

    public void getRouteWaysFromJson(String routeName, String filename) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:routes/" + filename);

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new FileReader(file));
        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);

        Route route = routeRepository.save(new Route(routeName));

        for (ElementDTO element : result.elements) {
            Way way = new Way(element.id, element.tags.name);
            way = wayRepository.save(way);

            for (int i = 0; i < element.nodes.length; i++) {
//                Coordinate point = new Coordinate(element.geometry[i].lat, element.geometry[i].lon);
                GeometryFactory geometryFactory = new GeometryFactory();
                Point point = geometryFactory.createPoint(new Coordinate(element.geometry[i].lat, element.geometry[i].lon));
                Node node = new Node(element.nodes[i], point);
                node = nodeRepository.save(node);

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
}