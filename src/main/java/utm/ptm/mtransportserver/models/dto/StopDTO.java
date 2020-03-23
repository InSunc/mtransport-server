package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import utm.ptm.mtransportserver.models.db.Node;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StopDTO {
    private String name;
    private CoordinateDTO location;
}
