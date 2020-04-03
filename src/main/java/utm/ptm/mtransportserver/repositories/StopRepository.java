package utm.ptm.mtransportserver.repositories;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;

import java.util.List;
import java.util.Optional;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    @Query(value = "SELECT * FROM stops ORDER BY location <-> st_setsrid(?1, 0) LIMIT 1;", nativeQuery = true)
    Optional<Stop> findNearest(Point point);

    List<Stop> findAllById(Iterable<Long> stopIds);
}
