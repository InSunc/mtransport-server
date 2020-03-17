package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteWay;

import java.util.List;

@Repository
public interface RouteWayRepository extends JpaRepository<RouteWay, Long> {
    public List<RouteWay> findAllByRoute(Route route);
}
