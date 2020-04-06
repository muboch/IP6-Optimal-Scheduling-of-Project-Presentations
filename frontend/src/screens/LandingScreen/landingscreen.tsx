import React from "react";
import { Button } from "@material-ui/core";
import { useGStyles } from "../../theme";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../constants";

const LandingScreen: React.FC = (): JSX.Element => {
  const styles = useGStyles();
  const [location, setLocation] = useLocation();


    return (
    <div className={styles.centerFlexDiv}>
        <Button
            className={styles.primaryButton}
            target="_blank"
            href={`${process.env.REACT_APP_API_ENDPOINT}/api/plannings/example`}
        >
            Beispiel Dateien
        </Button>
      <Button
        className={styles.primaryButton}
        onClick={() => {
          setLocation(SCREENROUTES.createPlanning);
        }}
      >
        Neue Planung erstellen
      </Button>
      <Button
        className={styles.primaryButton}
        onClick={() => {
          setLocation(SCREENROUTES.listPlanning);
        }}
      >
        Plannungen Anzeigen
      </Button>
    </div>
  );
};

export default LandingScreen;
