package utm.ptm.mtransportserver.models.db;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transports")
public class Transport {
    @Id
    @Column
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Route route;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn
//    private TransportType transportType;
}
