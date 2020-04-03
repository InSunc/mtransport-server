package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;

import java.util.HashMap;
import java.util.LinkedHashSet;

@AllArgsConstructor
@NoArgsConstructor
public class TripDTO {
    HashMap<LinkedHashSet<RouteDTO>, LinkedHashSet<StopDTO>> trip;
}
