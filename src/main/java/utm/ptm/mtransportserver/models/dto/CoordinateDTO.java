package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinateDTO {
    private double lat;
    private double lon;

    public CoordinateDTO(Point point) {
        this.lat = point.getCoordinate().getY();
        this.lon = point.getCoordinate().getX();
    }
}
