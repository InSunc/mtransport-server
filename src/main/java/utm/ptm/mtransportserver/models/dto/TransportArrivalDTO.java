package utm.ptm.mtransportserver.models.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import utm.ptm.mtransportserver.models.db.TransportArrival;

import java.time.LocalDateTime;

public class TransportArrivalDTO {
    public long transportId;
    public String routeId;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime timestamp;

    public TransportArrivalDTO(TransportArrival transportArrival) {
        this.transportId = transportArrival.getTransport().getId();
        this.routeId = transportArrival.getTransport().getRoute().getId();
        this.timestamp = transportArrival.getTimestamp();
    }
}
