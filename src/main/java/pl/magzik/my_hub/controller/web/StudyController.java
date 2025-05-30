package pl.magzik.my_hub.controller.web;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.print.Pageable;
import java.util.List;

/**
 * TODO; Work in progress
 * @author Maksymilian Strzelczak
 * */
@Controller
@RequestMapping("/studies")
public class StudyController {

    @GetMapping
    public String getSubjects(Model model) {
        model.addAttribute("page", new PageImpl<>(List.of())); // TODO: Temporary
        return "studies";
    }

}
