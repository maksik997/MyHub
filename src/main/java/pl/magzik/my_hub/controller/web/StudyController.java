package pl.magzik.my_hub.controller.web;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO; Work in progress
 * @author Maksymilian Strzelczak
 * */
@Controller
@RequestMapping("/studies")
public class StudyController {

    @GetMapping
    public String getSubjects(Model model) {
        Map<String, String> sortOptions = new HashMap<>();
        sortOptions.put("code", "Code");
        sortOptions.put("name", "Name");
        sortOptions.put("creationDate", "Creation date");
        sortOptions.put("modificationDate", "Modification date");
        model.addAttribute("sortOptions", sortOptions);

        model.addAttribute("orderBy", "desc"); // TODO: Temporary
        model.addAttribute("sortBy", "creationDate"); // TODO: Temporary
        model.addAttribute("page", new PageImpl<>(List.of())); // TODO: Temporary
        return "studies";
    }

}
