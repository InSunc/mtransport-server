package utm.ptm.mtransportserver.controllers;

import jdk.vm.ci.meta.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.ptm.mtransportserver.models.db.Ticket;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.models.dto.TicketDTO;
import utm.ptm.mtransportserver.services.TicketService;
import utm.ptm.mtransportserver.services.TransportService;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    private TransportService transportService;
    @Autowired
    private TicketService ticketService;

    @GetMapping("/{transportId}")
    public ResponseEntity<TicketDTO> getTicket(@PathVariable(name = "transportId") Long transportId) {
        Transport transport = transportService.findById(transportId).orElse(null);
        if (transport == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Ticket ticket = new Ticket();
        ticket.setTransport(transport);
        ticket.setCreationTime(LocalDateTime.now());

        ticket = ticketService.save(ticket);

        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.id = ticket.getId();
        ticketDTO.routeId = transport.getRoute().getId();
        ticketDTO.transportId = transport.getId();
        ticketDTO.creationTime = ticket.getCreationTime();

        return ResponseEntity.status(HttpStatus.OK).body(ticketDTO);
    }
}
