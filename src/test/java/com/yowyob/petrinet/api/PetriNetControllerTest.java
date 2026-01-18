package com.yowyob.petrinet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.petrinet.api.dto.ArcDTO;
import com.yowyob.petrinet.api.dto.NetDTO;
import com.yowyob.petrinet.api.dto.TokenDTO;
import com.yowyob.petrinet.api.dto.TransitionDTO;
import com.yowyob.petrinet.service.PetriNetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetriNetController.class)
class PetriNetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetriNetService petriNetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createNet_ShouldReturnId() throws Exception {
        NetDTO netDto = new NetDTO();
        netDto.places = List.of("p1");
        netDto.transitions = List.of(new TransitionDTO("t1", "T1", 0, 10));
        netDto.arcs = List.of(new ArcDTO("p1", "t1", "INPUT", 1));

        Mockito.when(petriNetService.createNet(any(NetDTO.class))).thenReturn("net-123");

        mockMvc.perform(post("/api/nets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(netDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("net-123"));
    }

    @Test
    void fireTransition_ShouldReturnOk() throws Exception {
        Map<String, List<TokenDTO>> binding = Map.of("p1", List.of(new TokenDTO("A", 0)));

        mockMvc.perform(post("/api/nets/net-123/fire/t1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(binding)))
                .andExpect(status().isOk());
    }
}
