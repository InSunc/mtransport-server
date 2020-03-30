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
    private double latitude;
    private double longitude;

    public CoordinateDTO(Point point) {
        this.latitude = point.getCoordinate().getY();
        this.longitude = point.getCoordinate().getX();
    }

    public CoordinateDTO(Coordinate coordinate) {
        this.latitude = coordinate.y;
        this.longitude = coordinate.x;
    }
}
