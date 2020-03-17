package utm.ptm.mtransportserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utm.ptm.mtransportserver.models.db.Node;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    @Query(value = "SELECT * FROM nodes\n" +
                    " ORDER BY location <-> st_setsrid(st_makepoint(?1, ?2), 0)\n" +
                    " LIMIT 1;\n",
            nativeQuery = true)
    public Node findNearest(double lat, double lon);
}
