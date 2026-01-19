package com.yowyob.petrinet.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("petri_nets")
public class PetriNetEntity {
    @Id
    private UUID id;
    private String name;

    @Column("current_net_time")
    private Long currentTime;
}
