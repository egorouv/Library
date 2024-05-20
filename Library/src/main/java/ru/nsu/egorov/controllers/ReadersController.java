package ru.nsu.egorov.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nsu.egorov.models.Readers;
import ru.nsu.egorov.services.ReadersService;

import java.util.List;

@Controller
@RequestMapping("/readers")
public class ReadersController {

    @Autowired
    private ReadersService readersService;

//    @GetMapping
//    public String getAllReaders(Model model) {
//        List<Readers> readers = readersService.getAllReaders();
//        model.addAttribute("readers", readers);
//        return "readers";
//    }

    @GetMapping
    public String getReadersByFeature(Model model) {
        List<Readers> readers = readersService.getReadersByFeature();
        model.addAttribute("readers", readers);
        return "readers/list";
    }

}
