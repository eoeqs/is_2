package eoeqs.controller;

import eoeqs.model.Human;
import eoeqs.service.HumanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/humans")
public class HumanController {

    private final HumanService humanService;

    public HumanController(HumanService humanService) {
        this.humanService = humanService;
    }

    @PostMapping
    public ResponseEntity<Human> createHuman(@RequestBody Human human) {
        Human createdHuman = humanService.createHuman(human);
        return ResponseEntity.ok(createdHuman);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Human>> getHumanById(@PathVariable Long id) {
        Optional<Human> human = humanService.getHumanById(id);
        return ResponseEntity.ok(human);
    }

    @GetMapping
    public ResponseEntity<List<Human>> getAllHumans() {
        List<Human> humans = humanService.getAllHumans();
        return ResponseEntity.ok(humans);
    }
}
