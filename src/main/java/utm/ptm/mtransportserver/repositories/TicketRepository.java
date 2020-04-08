package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Ticket;
import utm.ptm.mtransportserver.models.db.Transport;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query(value = "select * from tickets where creation_time >= current_date and transport_id = ?1 and expiration_time is NULL;",
    nativeQuery = true)
    List<Ticket> getNumberOfPeople(long transportId);
}
