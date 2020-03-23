package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteStop;
import utm.ptm.mtransportserver.models.db.Stop;

import java.util.List;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    public List<RouteStop> findAllByRoute(Route route);
}
