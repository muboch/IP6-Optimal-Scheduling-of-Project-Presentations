package ch.fhnw.ip6.api;

import ch.fhnw.ip6.common.dto.Planning;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class SolverContext {

    private String logFileName;

    private boolean isSolving;

    private Planning planning;

    /**
     * Compares received planning with existing planning. Keeps the planning with the better score.
     *
     * @param planning
     */
    public void saveBestPlanning(Planning planning) {
        if (this.planning== null || this.planning.getCost() > planning.getCost()) {
            this.planning = planning;
        }
    }

    /**
     * Clears an existing planning and resets the isSolving to false
     */
    public void reset(){
        isSolving = false;
        planning = null;
        logFileName = null;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }


}
