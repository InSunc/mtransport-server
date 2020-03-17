package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    public Route findByName(String routeName);
    public Optional<Route> findById(Long id);
}
