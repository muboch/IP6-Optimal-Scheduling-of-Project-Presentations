package ch.fhnw.ip6.api;

import ch.fhnw.ip6.common.dto.Planning;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class SolverContext {


    private boolean isSolving;

    private Planning planning;

    /**
     * Compares received planning with existing planning. Keeps the planning with the better score.
     *
     * @param planning
     * @return current Planning with best score
     */
    public Planning saveBestPlanning(Planning planning) {
        if (this.planning== null || this.planning.getCost() > planning.getCost()) {
            this.planning = planning;
        }
        return this.planning;
    }

    /**
     * Clears an existing planning and resets the isSolving to false
     */
    public void reset(){
        isSolving = false;
        planning = null;
    }

}
