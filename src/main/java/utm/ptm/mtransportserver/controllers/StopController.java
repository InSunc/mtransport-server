package utm.ptm.mtransportserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import java.io.FileNotFoundException;
import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stops")
public class StopController {
    @Autowired
    private OverpassDataParser overpassDataParser;

    @GetMapping
    public String getStops() throws IOException {
        try {
            overpassDataParser.getStopsFromJson("t2","t2/t2-stops.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "Success";
    }
}
