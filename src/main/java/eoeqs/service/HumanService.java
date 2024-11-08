package eoeqs.service;

import eoeqs.model.Human;
import eoeqs.repository.HumanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HumanService {

    private final HumanRepository humanRepository;

    public HumanService(HumanRepository humanRepository) {
        this.humanRepository = humanRepository;
    }

    public Human createHuman(Human human) {
        return humanRepository.save(human);
    }

    public Optional<Human> getHumanById(Long id) {
        return humanRepository.findById(id);
    }

    public Human updateHuman(Long id, Human human) {
        return humanRepository.findById(id)
                .map(existingHuman -> {
                    existingHuman.setHeight(human.getHeight());
                    return humanRepository.save(existingHuman);
                }).orElseThrow(() -> new RuntimeException("Human not found"));
    }

    public void deleteHuman(Long id) {
        humanRepository.deleteById(id);
    }

    public List<Human> getAllHumans() {
        return humanRepository.findAll();
    }
}
