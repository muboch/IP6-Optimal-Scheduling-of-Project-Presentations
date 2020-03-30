package ch.fhnw.ip6.api;


import ch.fhnw.ip6.common.dto.Planning;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public abstract class AbstractSolver implements SolverApi {

    protected final SolverContext solverContext;



}
