/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState, useEffect } from "react";
import { getIsSolving } from "../Services/planningService";

export type planningState = {
  solving: boolean;
  progress: number;
};

const planningState = () => {
  const [planningState, setPlanningState] = useState<planningState>({
    solving: false,
    progress: 0,
  });

  useEffect(() => {
    const load = async () => {
      const ps = await getIsSolving();
      setPlanningState(ps);
    };
    load();
    const interval = setInterval(load, 2500);
    return () => {
      clearInterval(interval);
    };
  }, []);

  return planningState;
};
const PlanningContainer = createContainer(planningState);
export default PlanningContainer;
