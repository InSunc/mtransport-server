package utm.ptm.mtransportserver.models.dto;

public class StopClientsDTO {
    public int hour;
    public int clientsNr;

    public StopClientsDTO(int hour, int clientsNr) {
        this.hour = hour;
        this.clientsNr = clientsNr;
    }
}
