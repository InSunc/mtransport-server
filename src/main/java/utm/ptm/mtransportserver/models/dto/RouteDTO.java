package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.dto.WayDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {
    public String id;
    public String name;
    public List<WayDTO> ways;
    public List<StopDTO> stops;

    public RouteDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public RouteDTO(List<WayDTO> ways, List<StopDTO> stops) {
        this.ways = ways;
        this.stops = stops;
    }
}
