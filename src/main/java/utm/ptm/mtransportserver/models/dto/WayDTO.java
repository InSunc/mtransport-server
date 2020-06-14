package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import utm.ptm.mtransportserver.models.db.Way;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
public class WayDTO {
    public String name;
    public List<CoordinateDTO> points;

    public WayDTO(Way way) {
        name = way.getName();
        points = new ArrayList<>();
        LineString lineString = way.getPoints();
        Coordinate[] coordinates = lineString.getCoordinates();
        for (Coordinate coordinate : coordinates) {
            points.add(new CoordinateDTO(coordinate));
        }
    }
}
