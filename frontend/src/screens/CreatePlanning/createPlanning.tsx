import React from "react";
import { Button } from "@material-ui/core";
import { useGStyles } from "../../theme";
import { useLocation } from "wouter";
import { SCREENROUTES, APIROUTES } from "../../constants";

const CreatePlanning: React.FC = (): JSX.Element => {
  const styles = useGStyles();
  const [location, setLocation] = useLocation();

  return (
    <div className={styles.centerFlexDiv}>
      <div className={styles.columnFlexDiv}>
        <div>Hier kommt eine Table hin. Muss ich noch implementieren ;) Auch den PlanningService...</div>

        <Button
          className={styles.primaryButton}
          target="_blank"
          href={`${APIROUTES.planning}/example`}
        >
          Planung erstellen
        </Button>
      </div>
    </div>
  );
};

export default CreatePlanning;
