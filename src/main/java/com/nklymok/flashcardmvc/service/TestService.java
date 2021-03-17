package com.nklymok.flashcardmvc.service;

import com.nklymok.flashcardmvc.model.Flashcard;
import com.nklymok.flashcardmvc.model.Test;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TestService {

    public void shuffleFlashcards(Test test) {
        Collections.shuffle(test.getFlashcards());
    }

    public List<Flashcard> getIncorrect(Test test) {
        List<Flashcard> flashcards = test.getFlashcards();
        List<Flashcard> result = new ArrayList<>();

        for (Flashcard f : flashcards) {
            if (f == null || f.getAnswer() == null || f.getQuestion() == null) continue;
                if (!f.getUserGuess().trim().equalsIgnoreCase(f.getAnswer().trim())) {
                    result.add(f);
                }
        }

        return result;
    }

}
