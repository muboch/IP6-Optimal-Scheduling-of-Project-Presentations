import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import { Button } from "@material-ui/core";
import { useGStyles } from "../theme";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../constants";

export interface SidebarProps {}

const Sidebar: React.SFC<SidebarProps> = () => {
  const useStyles = makeStyles({
    sidebar: {
      width: 210,
      height: "100%",
      backgroundColor: "#eee",
      marginRight: "10px",
      padding: "5px",
      display: "flex",
      alignContent: "center",
      alignItems: "center",
      justifyContent: "center",
      flexDirection: "column",
    },
    spacer: {
      padding: 0,
      margin: 0,
      width: "100%",
      borderBottom: "1px solid black",
    },
    button: {
      width: "100%",
      // marginRight: "2px",
    },
  });
  const styles = useStyles();
  const gStyles = useGStyles();
  const [location, setLocation] = useLocation();

  return (
    <div className={styles.sidebar}>
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.landingScreen);
        }}
      >
        Startseite
      </Button>

      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.uploadFiles);
        }}
      >
        Daten hochladen
      </Button>
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.listPlanning);
        }}
      >
        Erstellte Planungen
      </Button>
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.createPlanning);
        }}
      >
        Planung erstellen
      </Button>

      <p className={styles.spacer} />
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.presentations);
        }}
      >
        Präsentationen
      </Button>
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.lecturers);
        }}
      >
        Lehrpersonen
      </Button>
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.rooms);
        }}
      >
        Räume
      </Button>
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.timeslots);
        }}
        disabled
      >
        Zeitslots
      </Button>
      <Button
        className={`${gStyles.primaryButton} ${styles.button}`}
        onClick={() => {
          setLocation(SCREENROUTES.offtimes);
        }}
        disabled
      >
        Sperrzeiten
      </Button>
    </div>
  );
};

export default Sidebar;
