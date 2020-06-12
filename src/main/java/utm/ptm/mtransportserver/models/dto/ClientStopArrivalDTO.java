package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class ClientStopArrivalDTO {
    public long stopId;
    public boolean arrived;
    public int hour;
    public int number;
}
