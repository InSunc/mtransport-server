package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.RouteWay;

@Repository
public interface RouteWayRepository extends JpaRepository<RouteWay, Long> {
}
