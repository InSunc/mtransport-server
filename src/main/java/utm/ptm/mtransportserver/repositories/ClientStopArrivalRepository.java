package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.ClientStopArrival;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientStopArrivalRepository extends JpaRepository<ClientStopArrival, Long> {
    List<ClientStopArrival> findAllByStop(Stop stop);
}
