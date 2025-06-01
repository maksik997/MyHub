package pl.magzik.my_hub.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.magzik.my_hub.dto.studies.SubjectRequest;
import pl.magzik.my_hub.service.studies.SubjectService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/add")
    public String addSubject(Model model) { /// <- empty form
        var empty = new SubjectRequest(null, null, null);
        model.addAttribute("subject", empty);
        return "add_subject";
    }

    @PostMapping("/add")
    public String addSubject(
            @Valid @ModelAttribute("subject") SubjectRequest subjectRequest,
            BindingResult bindingResult,
            Model model
    ) { /// <- subject adding handling
        if (bindingResult.hasErrors()) {
            log.warn("Validation has failed: {}", bindingResult.getAllErrors());
            model.addAttribute("message", "Invalid data: %s".formatted(bindingResult
                    .getAllErrors()
                    .stream()
                    .map(err -> "%s %s".formatted(err.getCode(), err.getDefaultMessage()))
                    .collect(Collectors.joining(",\n")))
            );

            return "add_subject";
        }

        var subject = subjectService.saveSubject(subjectRequest);
        model.addAttribute("message", "Subject '%s' has been successfully added.".formatted(subject.code()));

        var empty = new SubjectRequest(null, null, null);
        model.addAttribute("subject", empty);

        return "add_subject";
    }

//    TODO: PUT, DELETE ...

}
