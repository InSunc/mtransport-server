package utm.ptm.mtransportserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.repositories.StopRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StopService {
    @Autowired
    private StopRepository stopRepository;

    public Stop save(Stop stop) {
        return stopRepository.save(stop);
    }

// TODO: check if it makes sense
    public List<Stop> saveAll(List<Stop> stops) {
        List<Long> stopIds = new ArrayList<>();
        stops.forEach(x -> stopIds.add(x.getStopNode().getId()));

        List<Stop> repoStops = stopRepository.findAllById(stopIds);

        if (!repoStops.isEmpty()) {
            for (int i = 0; i < repoStops.size(); i++) {
                repoStops.get(i).setName(stops.get(i).getName());
                repoStops.get(i).setRouteNode(stops.get(i).getRouteNode());
            }
            return stopRepository.saveAll(repoStops);
        } else {
            return stopRepository.saveAll(stops);
        }
    }


}
