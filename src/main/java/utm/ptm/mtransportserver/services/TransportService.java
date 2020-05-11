package utm.ptm.mtransportserver.services;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.operation.linemerge.LineSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.*;
import utm.ptm.mtransportserver.repositories.TicketRepository;
import utm.ptm.mtransportserver.repositories.TransportArrivalRepository;
import utm.ptm.mtransportserver.repositories.TransportRepository;

import java.util.*;

@Service
public class TransportService {

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private WayService wayService;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransportArrivalRepository transportArrivalRepository;

    @Autowired
    private TicketRepository ticketRepository;


    public int getNrOfProple(long transportId) {
        List<Ticket> tickets = ticketRepository.getNumberOfPeople(transportId);
        return tickets.size();
    }

    public Transport save(Transport transport) {
        return transportRepository.save(transport);
    }


    public Optional<Transport> findById(Long id) {
        return transportRepository.findById(id);
    }

    public TransportArrival save(TransportArrival transportArrival) {
        return transportArrivalRepository.save(transportArrival);
    }

    public Iterable<Transport> findAllByRoute(Route route) {
        return transportRepository.findAllByRoute(route);
    }
}
