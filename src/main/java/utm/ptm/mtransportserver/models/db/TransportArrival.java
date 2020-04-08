package utm.ptm.mtransportserver.models.db;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="transport_arrivals")
public class TransportArrival {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Transport transport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Stop stop;

    @Column
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime timestamp;

    public TransportArrival(Transport transport, Stop stop, LocalDateTime timestamp) {
        this.transport = transport;
        this.stop = stop;
        this.timestamp = timestamp;
    }
}
