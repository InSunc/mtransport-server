package utm.ptm.mtransportserver.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteNode;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.repositories.NodeRepository;
import utm.ptm.mtransportserver.repositories.RouteNodesRepository;
import utm.ptm.mtransportserver.repositories.RouteRepository;
import utm.ptm.mtransportserver.repositories.StopRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


@Component
public class OverpassDataParser {
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteNodesRepository routeNodesRepository;

    @Autowired
    private StopRepository stopRepository;

    /*
     * TODO: Filter nodes with empty names
     *       Write a lower level parser
     * Overpass query: node(46.9650, 28.7763, 47.0704, 28.9277)[highway=bus_stop];
     */
    public void getStopsFromJson(String filename) throws IOException {
        File file = ResourceUtils.getFile("classpath:static/" + filename);

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new FileReader(file));
        OverpassResultDTO result = gson.fromJson(jsonReader, OverpassResultDTO.class);

        List<Node> nodes = nodeRepository.findAll();

        for (ElementDTO element : result.elements) {
            if (element.tags.name == null)
                continue;

            Node stopNode = new Node();
            stopNode.setId(element.id);
            stopNode.setLat(element.lat);
            stopNode.setLng(element.lon);

            if (!nodes.contains(stopNode)) {
                stopNode = nodeRepository.save(stopNode);
            }

            Stop stop = new Stop();
            stop.setName(element.tags.name);
            stop.setStopNode(stopNode);
            stopRepository.save(stop);
        }
    }

    public void getRouteFromJson(String filename) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:static/" + filename);

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new FileReader(file));
        GeoJsonResultDTO result = gson.fromJson(jsonReader, GeoJsonResultDTO.class);

        GeometryDTO geometry = result.features[0].geometry; // first element is the LineString
        List<Node> nodes = nodeRepository.findAll();

        Route route = new Route();
        route.setName(result.features[0].properties.name);
        route = routeRepository.save(route);

        if (geometry.type.equals("LineString")) {
            for (double[] coordinate : geometry.coordinates) {
                Node node = new Node(coordinate[1], coordinate[0]);
                if (!nodes.contains(node)) {
                    node = nodeRepository.save(node);
                    RouteNode routeNode = new RouteNode();
                    routeNode.setRoute(route);
                    routeNode.setNode(node);
                    routeNodesRepository.save(routeNode);
                }
            }
        }
    }
}


class GeoJsonResultDTO {
    FeatureDTO[] features;
}

class FeatureDTO {
    GeometryDTO geometry;
    TagsDTO properties;
}


class GeometryDTO {
    String type;
    double[][] coordinates;
}


class OverpassResultDTO {
    double version;
    String generator;
    ElementDTO[] elements;
}

class ElementDTO {
    long id;
    String type;
    double lat;
    double lon;
    TagsDTO tags;
    MemberDTO[] members;
}

class MemberDTO {
    String type;
    long ref;
}

class TagsDTO {
    String name;
    @SerializedName("name:ru")
    String nameRU;
}

class WayDTO {
    long id;
    long[] nodes;
}