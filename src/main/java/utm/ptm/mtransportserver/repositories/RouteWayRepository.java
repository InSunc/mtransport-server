package utm.ptm.mtransportserver.repositories;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.RouteWay;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteWayRepository extends JpaRepository<RouteWay, Long> {
    @Query(value = "select * from route_ways where route_ways.route_id in (?1)", nativeQuery = true)
    List<RouteWay> findAllByRoutes(Iterable<Route> routes);
    List<RouteWay> findAllByRoute(Route route);
    List<RouteWay> findAllByWay(Way way);
}
