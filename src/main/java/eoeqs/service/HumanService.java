package eoeqs.service;

import eoeqs.dao.HumanDAO;
import eoeqs.model.Human;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HumanService {
    private final HumanDAO humanDAO;

    public Human getHumanById(Long id) {
        Optional<Human> human = humanDAO.findById(id);
        if (human.isEmpty()) {
            throw new RuntimeException("Human not found");
        }
        return human.get();
    }

    public Human createHuman(Human human) {
        humanDAO.save(human);
        return human;
    }
}