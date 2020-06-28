/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState, useEffect } from "react";
import { getIsSolving } from "../Services/planningService";

const planningState = () => {
  const [isSolving, setIsSolving] = useState<boolean>(false);

  useEffect(() => {
    const load = async () => {
      const is = await getIsSolving();
      setIsSolving(is);
    };
    load();
    const interval = setInterval(load, 2500);
    return () => {
      clearInterval(interval);
    };
  }, []);

  return {
    isSolving,
  };
};
const PlanningContainer = createContainer(planningState);
export default PlanningContainer;
