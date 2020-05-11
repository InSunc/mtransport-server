package utm.ptm.mtransportserver.repositories;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.List;
import java.util.Optional;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    @Query(value = "SELECT * FROM stops ORDER BY location <-> st_setsrid(?1, 0) LIMIT 1;", nativeQuery = true)
    Optional<Stop> findNearest(Point point);

    @Query(value = "SELECT * FROM stops where side = ?2 ORDER BY location <-> st_setsrid(?1, 0) LIMIT 1;", nativeQuery = true)
    Optional<Stop> findNearest(Point point, int side);

    @Query(value = "select * from stops join route_stops on route_stops.stop_id = stops.id where ST_Distance(?1, stops.\"location\", false) <= (?2) limit 1;",
            nativeQuery = true)
    Optional<Stop> findByDistance(Point point, int maxDistance);

    @Query(value = "select stops.id, stops.name, stops.location, stops.side, stops.way_id from route_stops join stops on route_stops.stop_id = stops.id where ST_Distance(?1, stops.\"location\", false) <= (?3) and route_stops.route_id = ?2 limit 1;",
            nativeQuery = true)
    Optional<Stop> findByDistance(Point point, String routeId, int maxDistance);

    @Query(value = "select * from ways join stops on stops.way_id = ways.id where ways.id in (?1) and stops.side = ?2", nativeQuery = true)
    List<Stop> findAllByWay(Iterable<Way> ways, int side);

    List<Stop> findAllById(Iterable<Long> stopIds);
}
