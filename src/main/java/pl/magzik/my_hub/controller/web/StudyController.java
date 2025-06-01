package pl.magzik.my_hub.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.magzik.my_hub.dto.studies.SubjectRequest;
import pl.magzik.my_hub.service.studies.SubjectService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO; Docs
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
        Objects.requireNonNull(pageable);
        Objects.requireNonNull(model);

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

        return "studies/list";
    }

    @GetMapping("/{id}")
    public String getSubject(
            @PathVariable Integer id,
            Model model
    ) {

        var subject = subjectService.findSubjectById(id);
        model.addAttribute("subject", subject);

        return "studies/view";
    }

    @GetMapping("/add")
    public String addSubject(Model model) { /// <- empty form
        Objects.requireNonNull(model);

        var empty = new SubjectRequest(null, null, null);
        if (!model.containsAttribute("subject")) {
            model.addAttribute("subject", empty);
        }
        return "studies/add";
    }


    @PostMapping("/add")
    public String addSubject(
            @Valid @ModelAttribute("subject") SubjectRequest subjectRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) { /// <- subject adding handling
        Objects.requireNonNull(subjectRequest);
        Objects.requireNonNull(bindingResult);
        Objects.requireNonNull(redirectAttributes);
        Objects.requireNonNull(model);

        if (handleValidation(bindingResult, model)) {
            redirectAttributes.addFlashAttribute("subject", subjectRequest);
            redirectAttributes.addFlashAttribute("message", model.getAttribute("message"));
            return "studies/add";
        }

        var subject = subjectService.saveSubject(subjectRequest);
        redirectAttributes.addAttribute("message", "Subject '%s' has been successfully added.".formatted(subject.code()));

        return "redirect:/studies/" + subject.id();
    }


    @PostMapping("/{id}/update")
    public String updateSubject(
            @PathVariable Integer id,
            @Valid @ModelAttribute("subject") SubjectRequest subjectRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(subjectRequest);
        Objects.requireNonNull(bindingResult);
        Objects.requireNonNull(redirectAttributes);
        Objects.requireNonNull(model);

        if (handleValidation(bindingResult, model)) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    model.getAttribute("message")
            );
            return "redirect:/studies/" + id;
        }

        var subject = subjectService.updateSubject(id, subjectRequest);
        redirectAttributes.addFlashAttribute(
                "message",
                "Subject %s has been successfully updated.".formatted(id)
        );

        return "redirect:/studies/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteSubject(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(redirectAttributes);
        Objects.requireNonNull(model);

        subjectService.deleteSubject(id);
        redirectAttributes.addFlashAttribute("message", "Subject has been deleted successfully.");

        return "redirect:/studies";
    }

    /* TODO: Add/Remove files, Open (download) files */

    private boolean handleValidation(BindingResult bindingResult, Model model) {
        Objects.requireNonNull(bindingResult);
        Objects.requireNonNull(model);

        if (bindingResult.hasErrors()) {
            log.warn("Validation has failed: {}", bindingResult.getAllErrors());
            model.addAttribute("message", "Invalid data: %s".formatted(bindingResult
                    .getAllErrors()
                    .stream()
                    .map(err -> "%s %s".formatted(err.getCode(), err.getDefaultMessage()))
                    .collect(Collectors.joining(",\n")))
            );
            return true;
        }
        return false;
    }

}
