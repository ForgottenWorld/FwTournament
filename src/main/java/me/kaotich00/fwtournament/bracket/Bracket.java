package me.kaotich00.fwtournament.bracket;

import java.util.Set;
import java.util.UUID;

public class Bracket {

    private Set<UUID> challengers;

    private Integer stepIdentifier;

    public Bracket(Set<UUID> challengers, Integer stepIdentifier) {
        this.challengers = challengers;
        this.stepIdentifier = stepIdentifier;
    }

    public Set<UUID> getChallengers() {
        return challengers;
    }

    public Integer getStepIdentifier() {
        return stepIdentifier;
    }

}
