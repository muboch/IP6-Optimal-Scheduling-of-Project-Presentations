import { useContainer } from "unstated-next";
import PlanningContainer from "../states/planningState";
import React from "react";
import { Typography, LinearProgress, Box } from "@material-ui/core";

const SolvingProgress: React.SFC = () => {
  const planningState = useContainer(PlanningContainer);

  return (
    <Box display="flex" width="100%" maxWidth="200px" alignItems="center">
      <Box width="100%" mr={1}>
        <LinearProgress variant="determinate" value={planningState.progress} />
      </Box>
      <Box minWidth={35}>
        <Typography variant="body2" color="textSecondary">{`${Math.round(
          planningState.progress
        )}%`}</Typography>
      </Box>
    </Box>
  );
};

export default SolvingProgress;
