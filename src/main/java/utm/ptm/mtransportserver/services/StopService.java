package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Node;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.repositories.NodeRepository;
import utm.ptm.mtransportserver.repositories.StopRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StopService {
    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private NodeRepository nodeRepository;

    public Stop save(Stop stop) {
        return stopRepository.save(stop);
    }

    public Optional<Stop> findById(Stop stop) {
        return stopRepository.findById(stop.getStopNode().getId());
    }

    public Optional<Stop> findById(Long id) {
        return stopRepository.findById(id);
    }

// TODO: check if it makes sense
    public List<Stop> saveAll(List<Stop> stops) {
        for (Stop stop : stops) {
            Node stopNode = stop.getStopNode();
            stopNode.setStopNode(stop);
            nodeRepository.save(stopNode);
        }

        return stops;
    }


}
