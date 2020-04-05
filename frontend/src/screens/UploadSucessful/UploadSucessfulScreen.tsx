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
          Die Dateien wurden hochgeladen und die Plannung wird erstellt. Nach
          Abschluss wird sie auf der Übersichtsseite sichtbar.
      </p>
      <Button
        className={gStyles.primaryButton}
        onClick={() => {
          setLocation(SCREENROUTES.listPlanning);
        }}
      >
        Zur Übersichtsseite / Planungen anzeigen
      </Button>
    </div>
  );
};

export default UploadSucessfulScreen;
