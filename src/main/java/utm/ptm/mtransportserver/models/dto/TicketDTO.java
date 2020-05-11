package utm.ptm.mtransportserver.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class TicketDTO {
    public long id;
    public LocalDateTime creationTime;
    public LocalDateTime expirationTime;
    public long transportId;
    public String routeId;
}
