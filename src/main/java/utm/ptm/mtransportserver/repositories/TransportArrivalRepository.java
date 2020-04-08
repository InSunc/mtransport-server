package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.TransportArrival;

import java.util.Optional;

@Repository
public interface TransportArrivalRepository extends JpaRepository<TransportArrival, Long> {

}
