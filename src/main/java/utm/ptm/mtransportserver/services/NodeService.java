package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.models.db.Way;
import utm.ptm.mtransportserver.models.db.WayNode;
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
}
