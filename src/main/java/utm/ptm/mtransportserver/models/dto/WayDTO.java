package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utm.ptm.mtransportserver.models.db.Way;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WayDTO {
    Long id;
    String name;

    public WayDTO(Way way) {
        this.id = way.getId();
        this.name = way.getName();
    }
}
