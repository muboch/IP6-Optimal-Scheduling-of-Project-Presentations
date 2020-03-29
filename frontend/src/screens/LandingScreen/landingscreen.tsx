import React from "react";
import {Button} from "@material-ui/core";
import {useGStyles} from "../../theme";
import {useLocation} from "wouter";

const LandingScreen: React.FC = (): JSX.Element => {
  const styles = useGStyles();
  const [location, setLocation] = useLocation();

  return (
    <div className={styles.centerFlexDiv}>
      <Button
        className={styles.primaryButton}
        onClick={() => {
          setLocation("/createPlanning");
        }}
      >
        Neue Planung erstellen
      </Button>
    </div>
  );
};

export default LandingScreen;
