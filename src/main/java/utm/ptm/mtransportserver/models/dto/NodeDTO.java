package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import utm.ptm.mtransportserver.models.db.Node;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NodeDTO {
    private Long id;
    private Point point;

    public NodeDTO(Node node) {
        id = node.getId();
        point = node.getLocation();
    }
}
