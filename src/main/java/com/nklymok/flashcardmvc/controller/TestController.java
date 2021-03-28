package com.nklymok.flashcardmvc.controller;

import com.nklymok.flashcardmvc.exception.TestNotFoundException;
import com.nklymok.flashcardmvc.model.Flashcard;
import com.nklymok.flashcardmvc.model.Test;
import com.nklymok.flashcardmvc.repository.TestRepository;
import com.nklymok.flashcardmvc.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("fcmvc")
@SessionAttributes("test")
public class TestController {

    private final TestRepository testRepository;
    private final TestService testService;

    @Autowired
    public TestController(TestRepository testRepository, TestService testService) {
        this.testRepository = testRepository;
        this.testService = testService;
    }

    @GetMapping
    public String getMenu() {
        return "index";
    }

    @GetMapping("build_test")
    public String buildTest(Model model) {
        Test test = new Test("New Test", new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            test.getFlashcards().add(new Flashcard());
        }
        model.addAttribute("test", test);
        return "build_test";
    }

    @GetMapping("pick_test")
    public String pickTest(Model model) {
        List<Test> tests = new ArrayList<>();
        testRepository.findAll().forEach(tests::add);
        model.addAttribute("tests", tests);
        return "pick_test";
    }

    @GetMapping("pick_test/{id}")
    public String pickTest(@PathVariable Long id, Model model) throws TestNotFoundException {
        Test test = testRepository.findById(id).orElseThrow(TestNotFoundException::new);
        testService.shuffleFlashcards(test);
        model.addAttribute("test", test);
        return "take_test";
    }

    @PostMapping("add_test")
    public String addTest(@ModelAttribute("test") @Valid Test test,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "build_test";

        }
        List <Flashcard> flashcards = test.getFlashcards();
        for (Flashcard f : flashcards) {
            f.setTest(test);
        }
        testRepository.save(test);
        return "redirect:/fcmvc/pick_test";
    }

    @GetMapping("edit_test/{id}")
    public String editTest(@PathVariable Long id, Model model) throws TestNotFoundException {
        Test test = testRepository.findById(id).orElseThrow(TestNotFoundException::new);
        model.addAttribute("test", test);
        return "edit_test";
    }

    @PostMapping("check_test")
    public String checkTest(Test test, Model model) {
        List<Flashcard> incorrect = testService.getIncorrect(test);
        model.addAttribute("test", test);
        model.addAttribute("incorrect", incorrect);
        return "test_result";
    }

    @GetMapping("delete_test/{id}")
    public String deleteTest(@PathVariable Long id) {
        testRepository.deleteById(id);
        return "redirect:/fcmvc/pick_test";
    }

    @ExceptionHandler(TestNotFoundException.class)
    public String pickTestError() {
        return "test_not_found";
    }

}
