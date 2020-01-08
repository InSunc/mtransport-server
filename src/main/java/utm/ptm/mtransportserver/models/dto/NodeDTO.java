package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utm.ptm.mtransportserver.models.db.Node;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NodeDTO {
    private Long id;
    private double lat;
    private double lng;

    public NodeDTO(Node node) {
        id = node.getId();
        lat = node.getLat();
        lng = node.getLng();
    }
}
