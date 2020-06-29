import React from "react";
import { Button } from "@material-ui/core";
import { useGStyles } from "../../theme";
import { useLocation } from "wouter";
import { SCREENROUTES, APIROUTES } from "../../constants";

const LandingScreen: React.FC = (): JSX.Element => {
  const styles = useGStyles();
  const [, setLocation] = useLocation();


    return (
    <div className={styles.centerFlexDiv}>
        <Button
            className={styles.primaryButton}
            target="_blank"
            href={`${APIROUTES.planning}/example`}
        >
            Beispieldateien herunterladen
        </Button>
      <Button
        className={styles.primaryButton}
        onClick={() => {
          setLocation(SCREENROUTES.uploadFiles);
        }}
      >
        Daten hochladen
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
