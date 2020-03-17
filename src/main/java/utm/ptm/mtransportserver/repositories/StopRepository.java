package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Stop;

import java.util.List;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    List<Stop> findAllById(Iterable<Long> stopIds);
}
