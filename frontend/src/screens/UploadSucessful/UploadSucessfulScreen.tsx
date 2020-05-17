import React from "react";
import { Button } from "@material-ui/core";
import { useGStyles } from "../../theme";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../constants";

const UploadSucessfulScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const [location, setLocation] = useLocation();

  return (
    <div className={gStyles.centerFlexDiv}>
      <p>
          Die Dateien wurden hochgeladen. Die Entitäten können nun bearbeitet werden.
      </p>
      <Button
        className={gStyles.primaryButton}
        onClick={() => {
          setLocation(SCREENROUTES.createPlanning);
        }}
      >
        Eine Planung erstellen
      </Button>
    </div>
  );
};

export default UploadSucessfulScreen;
