package utm.ptm.mtransportserver.repositories;

import org.locationtech.jts.geom.LineString;
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
public interface WayRepository extends JpaRepository<Way, Long> {
    @Query(value = "select ways.* \n" +
            "from pgr_dijkstra(\n" +
            "\t'SELECT ways.id as id, ways.source, ways.target, st_length(points, true) as cost FROM ways',\n" +
            "\t\n" +
            "\t(select \"source\" from ways\n" +
            "\torder by ST_Distance(\n" +
            "\t\tST_StartPoint(points),\n" +
            "        ST_SetSRID(ST_MakePoint(28.8254946, 46.9899328), 0),\n" +
            "        true) asc limit 1),\n" +
            "        \n" +
            "    (select \"source\" from ways\n" +
            "    order by ST_Distance(\n" +
            "        ST_StartPoint(points),\n" +
            "        ST_SetSRID(ST_MakePoint(28.8674648, 47.0584805), 0),\n" +
            "        true\n" +
            "   ) asc\n" +
            "   LIMIT 1), false\n" +
            "   \n" +
            ") as pt\n" +
            "join ways on pt.edge = ways.id;\n", nativeQuery = true)
    List<Way> findWay();

    @Query(value = "with rways as (select * from ways join route_ways on ways.id = route_ways.id and route_ways.route_id = ?1)\n" +
            "select * from rways order by rways.points <-> ?2 limit 1;", nativeQuery = true)
    Optional<Way> findNearest(String routeId, Point stopLocation);

}
