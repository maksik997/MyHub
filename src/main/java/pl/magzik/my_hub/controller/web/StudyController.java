package pl.magzik.my_hub.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.magzik.my_hub.service.studies.SubjectService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO; Work in progress
 * @author Maksymilian Strzelczak
 * */
@Controller
@RequestMapping("/studies")
@RequiredArgsConstructor
@Slf4j
public class StudyController {

    private final SubjectService subjectService;

    @GetMapping
    public String getSubjects(Pageable pageable, Model model) {
        Map<String, String> sortOptions = new HashMap<>();
        sortOptions.put("code", "Code");
        sortOptions.put("name", "Name");
        sortOptions.put("creationDate", "Creation date");
        sortOptions.put("modificationDate", "Modification date");
        model.addAttribute("sortOptions", sortOptions);

        Sort.Order order = pageable
                .getSort()
                .get()
                .findFirst()
                .orElse(null);

        model.addAttribute("sortBy", order != null ? order.getProperty() : "");
        model.addAttribute("orderBy",  order != null ? order.getDirection().name().toLowerCase() : "");

        var page = subjectService.findAllSubjects(pageable);
        model.addAttribute("page", page);

        return "studies";
    }

}
