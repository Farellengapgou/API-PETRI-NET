package com.yowyob.petrinet.persistence.repository;

import com.yowyob.petrinet.persistence.entity.*;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface PetriNetRepository extends ReactiveCrudRepository<PetriNetEntity, UUID> {
}

public interface PlaceRepository extends ReactiveCrudRepository<PlaceEntity, Long> {
    Flux<PlaceEntity> findAllByNetId(UUID netId);
}

public interface TransitionRepository extends ReactiveCrudRepository<TransitionEntity, Long> {
    Flux<TransitionEntity> findAllByNetId(UUID netId);
}

public interface ArcRepository extends ReactiveCrudRepository<ArcEntity, Long> {
    Flux<ArcEntity> findAllByNetId(UUID netId);
}

public interface TokenRepository extends ReactiveCrudRepository<TokenEntity, Long> {
    Flux<TokenEntity> findAllByNetId(UUID netId);

    reactor.core.publisher.Mono<Void> deleteAllByNetId(UUID netId);
}
