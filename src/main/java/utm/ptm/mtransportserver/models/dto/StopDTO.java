package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utm.ptm.mtransportserver.models.db.Stop;

@AllArgsConstructor
@NoArgsConstructor
public class StopDTO {
    public String name;
    public CoordinateDTO location;

    public StopDTO(Stop stop) {
        name = stop.getName();
        location = new CoordinateDTO(stop.getLocation().getCoordinate());
    }
}
