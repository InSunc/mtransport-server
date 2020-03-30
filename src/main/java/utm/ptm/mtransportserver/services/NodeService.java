package utm.ptm.mtransportserver.services;

import com.jayway.jsonpath.spi.cache.NOOPCache;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.repositories.NodeRepository;
import utm.ptm.mtransportserver.repositories.WayNodeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class NodeService {
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private WayNodeRepository wayNodeRepository;

    @Autowired
    private WayService wayService;

    public List<Node> saveAll(Iterable<Node> nodes) {
        return nodeRepository.saveAll(nodes);
    }

    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    public Node getNearest(Node node) {
        return nodeRepository.findNearest(node.getLocation().getCoordinate().getX(), node.getLocation().getCoordinate().getY());
    }

    public List<Node> getWayNodes(Way way) {
        List<WayNode> wayNodeList = wayNodeRepository.findAllByWay(way);
        List<Node> nodes = new ArrayList<>();
        wayNodeList.forEach(wayNode -> nodes.add(wayNode.getNode()));

        return nodes;
    }

//    public Node[] getMinMaxNodes(Way way) {
//        Node[] minMax = new Node[2];
//
//        List<Node> nodes = getWayNodes(way);
//
//        return minMax;
//    }

//    public List<List<Coordinate>> getDataForSimulation(Transport transport) {
//        Route route = transport.getRoute();
//        List<Way> ways = wayService.getRouteWays(route);
//        List<List<Coordinate>> coordinatesList = new ArrayList<>();
//
//        for (Way way : ways) {
//            List<Node> nodes = getWayNodes(way);
//            List<Coordinate> coordinates = new ArrayList<>();
//            coordinatesList.add(coordinates);
//            nodes.forEach(node -> coordinates.add(node.getLocation().getCoordinate()));
//        }
//
//        return coordinatesList;
//    }
}
