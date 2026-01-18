package com.yowyob.petrinet.api.dto;

import java.util.List;

public class NetDTO {
    public List<String> places; // Just IDs for now to keep it simple, or full objects
    public List<TransitionDTO> transitions;
    public List<ArcDTO> arcs;

    // Default constructor
    public NetDTO() {
    }

    public NetDTO(List<String> places, List<TransitionDTO> transitions, List<ArcDTO> arcs) {
        this.places = places;
        this.transitions = transitions;
        this.arcs = arcs;
    }
}
