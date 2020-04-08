package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class TripDTO {
    LinkedList<RouteDTO> routes;
    LinkedList<StopDTO> stops;
//    List<Way> ways;
}
