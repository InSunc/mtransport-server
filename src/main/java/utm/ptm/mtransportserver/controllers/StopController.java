package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stops")
public class StopController {


    @GetMapping
    public void getStops() throws IOException {

    }
}
