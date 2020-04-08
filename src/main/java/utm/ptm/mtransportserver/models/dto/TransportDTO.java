package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TransportDTO {
    public long id;
    public double latitude;
    public double longitude;
    public byte loadLevel;
}
