package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.repositories.NodeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class NodeService {
    @Autowired
    private NodeRepository nodeRepository;

    public List<Node> saveAll(Iterable<Node> nodes) {
        return nodeRepository.saveAll(nodes);
    }

    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    public Node findNearest(Node node) {
        return nodeRepository.findNearest(node.getLocation().getCoordinate().getX(), node.getLocation().getCoordinate().getY());
    }
}
