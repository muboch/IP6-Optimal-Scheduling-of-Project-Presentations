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
          Planung wird derzeit erstellt
        </Typography>
      </>
    );
  };

  const planingNotRunning = () => {
    return (
      <>
        <Typography variant="subtitle1" style={{ color: "green" }} gutterBottom>
          Planung wird derzeit nicht erstellt
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
