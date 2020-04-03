package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@AllArgsConstructor
@NoArgsConstructor
public class CoordinateDTO {
    public double latitude;
    public double longitude;

    public CoordinateDTO(Point point) {
        this.latitude = point.getCoordinate().getY();
        this.longitude = point.getCoordinate().getX();
    }

    public CoordinateDTO(Coordinate coordinate) {
        this.latitude = coordinate.y;
        this.longitude = coordinate.x;
    }

    public static Point toPoint(CoordinateDTO coordinateDTO) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(coordinateDTO.longitude, coordinateDTO.latitude);
        return geometryFactory.createPoint(coordinate);
    }
}
