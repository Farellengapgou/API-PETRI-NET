package com.yowyob.petrinet.api;

import com.yowyob.petrinet.api.dto.NetDTO;
import com.yowyob.petrinet.api.dto.NetStateDTO;
import com.yowyob.petrinet.api.dto.TokenDTO;
import com.yowyob.petrinet.service.PetriNetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nets")
public class PetriNetController {

    private final PetriNetService petriNetService;

    public PetriNetController(PetriNetService petriNetService) {
        this.petriNetService = petriNetService;
    }

    @PostMapping
    public ResponseEntity<String> createNet(@RequestBody NetDTO netDto) {
        String id = petriNetService.createNet(netDto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NetStateDTO> getNetState(@PathVariable String id) {
        return petriNetService.getNetState(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/fire/{transitionId}")
    public ResponseEntity<Void> fireTransition(
            @PathVariable String id,
            @PathVariable String transitionId,
            @RequestBody Map<String, List<TokenDTO>> binding) {
        try {
            petriNetService.fireTransition(id, transitionId, binding);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
