package com.yowyob.petrinet.service;

import com.yowyob.petrinet.application.CTPNService;
import com.yowyob.petrinet.api.dto.*;
import com.yowyob.petrinet.domain.model.PetriNet;
import com.yowyob.petrinet.domain.model.color.Token;
import com.yowyob.petrinet.domain.model.structure.Arc;
import com.yowyob.petrinet.domain.model.structure.ArcExpression;
import com.yowyob.petrinet.domain.model.structure.Place;
import com.yowyob.petrinet.domain.model.structure.Transition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PetriNetService {

    // Map: NetId -> CTPNService
    private final Map<String, CTPNService> runningNets = new ConcurrentHashMap<>();

    public String createNet(NetDTO netDto) {
        String id = UUID.randomUUID().toString();

        Set<Place> places = new HashSet<>();
        if (netDto.places != null) {
            for (String pId : netDto.places) {
                // Assuming Place constructor takes (id, name)
                places.add(new Place(pId, pId));
            }
        }

        Set<Transition> transitions = new HashSet<>();
        if (netDto.transitions != null) {
            for (TransitionDTO tDto : netDto.transitions) {
                transitions.add(new Transition(tDto.id, tDto.name, tDto.minFiringDelay, tDto.maxFiringDelay));
            }
        }

        Set<Arc> arcs = new HashSet<>();
        if (netDto.arcs != null) {
            for (ArcDTO aDto : netDto.arcs) {
                Arc.Type type = Arc.Type.valueOf(aDto.type);
                // Expression logic: map lookup
                // The 'weight' in DTO is used as the key in the binding map?
                // Or if weight is present, we produce that many tokens?
                // Let's use a SimpleExpression:
                // It expects the binding to be a Map<String, List<Token>>.
                // It looks up the key = placeId (default) or some specific key?
                // For simplicity: Binding is just List<Token> which is consumed/produced.
                // NOT GOOD for multiple arcs.

                // New Strategy: Binding is Map<String, List<Token>>.
                // Key = ArcId (but Arcs don't have IDs).
                // Key = PlaceId connected.

                ArcExpression expr = binding -> {
                    if (binding instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) binding;
                        String key = aDto.placeId; // Use placeId as key
                        Object val = map.get(key);
                        if (val instanceof List) {
                            return (List<Token<?>>) val;
                        }
                    }
                    return Collections.emptyList();
                };

                arcs.add(new Arc(aDto.placeId, aDto.transitionId, type, expr));
            }
        }

        PetriNet net = new PetriNet(places, transitions, arcs);
        CTPNService service = new CTPNService(net);
        runningNets.put(id, service);

        return id;
    }

    public Optional<NetStateDTO> getNetState(String id) {
        CTPNService service = runningNets.get(id);
        if (service == null)
            return Optional.empty();

        return Optional.of(convertStateToDTO(service));
    }

    private NetStateDTO convertStateToDTO(CTPNService service) {
        var state = service.getCurrentState();
        Map<String, List<TokenDTO>> markingMap = new HashMap<>();

        for (Place p : service.getModel().getPlaces()) {
            List<Token<?>> tokens = state.getTokens(p.getId());
            if (!tokens.isEmpty()) {
                List<TokenDTO> dtos = tokens.stream()
                        .map(t -> new TokenDTO(t.value(), t.creation_timestamp()))
                        .collect(Collectors.toList());
                markingMap.put(p.getId(), dtos);
            }
        }

        return new NetStateDTO(state.getCurrentTime(), markingMap);
    }

    public void fireTransition(String netId, String transitionId, Map<String, List<TokenDTO>> bindingDto) {
        CTPNService service = runningNets.get(netId);
        if (service == null)
            throw new IllegalArgumentException("Net not found");

        // Convert Binding DTO to Domain Binding
        Map<String, List<Token<?>>> domainBinding = new HashMap<>();
        bindingDto.forEach((k, v) -> {
            List<Token<?>> tokens = v.stream()
                    .map(t -> Token.create(t.value, t.creationTimestamp))
                    .collect(Collectors.toList());
            domainBinding.put(k, tokens);
        });

        service.fire(transitionId, domainBinding);
    }

    public PetriNet getModel(String netId) {
        if (!runningNets.containsKey(netId))
            return null;
        return runningNets.get(netId).getModel(); // need accessor in CTPNService
    }
}
