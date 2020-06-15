import { useContainer } from "unstated-next";
import PlanningContainer from "../states/planningState";
import React from "react";
import { CircularProgress, Typography } from "@material-ui/core";

const SolvingStatus: React.SFC = () => {
  const planningState = useContainer(PlanningContainer);

  const planingRunning = () => {
    return (
      <>
        <Typography variant="subtitle1" style={{ color: "red" }} gutterBottom>
          <CircularProgress
            size="1rem"
            style={{ color: "red", marginRight: "10px" }}
          />
          Es wird derzeit eine Planung erstellt
        </Typography>
      </>
    );
  };

  const planingNotRunning = () => {
    return (
      <>
        <Typography variant="subtitle1" style={{ color: "green" }} gutterBottom>
          Es wird derzeit keine Planung erstellt
        </Typography>
      </>
    );
  };

  return (
    <div>
      {planningState.isSolving ? planingRunning() : planingNotRunning()}
    </div>
  );
};

export default SolvingStatus;
