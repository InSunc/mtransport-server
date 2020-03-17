package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WayDTO {
    String name;
    List<Point> nodes;
}
