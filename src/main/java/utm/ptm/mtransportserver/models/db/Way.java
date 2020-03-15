package utm.ptm.mtransportserver.models.db;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ways")
public class Way {
    @Id
    @Column
    private Long id;

    @Column
    private String name;
}
