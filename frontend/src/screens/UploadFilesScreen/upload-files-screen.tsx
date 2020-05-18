import React, { useState, FormEvent } from "react";
import { Button, makeStyles, Snackbar, Typography } from "@material-ui/core";
import { useGStyles } from "../../theme";
import CheckCircleOutlineIcon from "@material-ui/icons/CheckCircleOutline";
import { Redirect, useLocation } from "wouter";
import { SCREENROUTES, APIROUTES } from "../../constants";
import MessageContainer from "../../states/messageState";

const useStyles = makeStyles((theme) => ({
  input: {
    display: "none",
  },
  button: {
    width: "400px",
  },
}));

const UploadFilesScreen: React.FC = (): JSX.Element => {
  const msgStore = MessageContainer.useContainer();
  const [location, setLocation] = useLocation();
  const gStyles = useGStyles();
  const styles = useStyles();
  const [files, setFiles] = useState<Files>({
    presentations: undefined,
    rooms: undefined,
    timeslots: undefined,
    teachers: undefined,
  });

  type UploadInfo = {
    key: keyof Files;
    label: string;
  };
  type Files = {
    presentations?: File;
    rooms?: File;
    timeslots?: File;
    teachers?: File;
    locktimes?: File;
  };

  const uploadInfos: Array<UploadInfo> = [
    { key: "presentations", label: "Präsentationen" },
    { key: "rooms", label: "Räume" },
    { key: "timeslots", label: "Zeitslots" },
    { key: "teachers", label: "Dozenten" },
    { key: "locktimes", label: "Sperrzeiten für Dozenten" },
  ];
  const getKeyValue = (key: keyof Files) => {
    return files[key] !== undefined;
  };

  const setFileForKey = (mykey: string, file: File) => {
    setFiles({ ...files, [`${mykey}`]: file });
  };

  const uploadFiles = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData();
    for (const [key, value] of Object.entries(files)) {
      formData.append(key, value as Blob);
      console.log(`${key}: ${value}`);
    }
    msgStore.setMessage(`Daten werden hochgeladen`);

    try {
      const res = await fetch(APIROUTES.planning, {
        // content-type header should not be specified!
        method: "POST",
        body: formData,
      });
      if (checkStatus(res)) {
        setLocation(SCREENROUTES.uploadSucessful);
      }
    } catch (error) {
      msgStore.setMessage(`Fehler beim upload der Dateien: ${error}`);
    }
  };

  const checkStatus = (res: Response) => {
    if (res.status >= 200 && res.status < 300) {
      return true;
    } else {
      if (res.status == 429) {
        let err = new Error(
          `Es wird bereits eine Planung erstellt. Bitte versuchen Sie es später nochmal`
        );
        throw err;
      }
      let err = new Error(`${res.status}: ${res.statusText}`);
      throw err;
    }
  };

  return (
    <>
      <form
        className={gStyles.columnFlexDiv}
        onSubmit={(e: FormEvent<HTMLFormElement>) => uploadFiles(e)}
      >
        {uploadInfos.map((u) => {
          return (
            <>
              <input
                accept=".xlsx"
                className={styles.input}
                id={`${u.key}-file`}
                type="file"
                onChange={(e) => {
                  setFileForKey(u.key, e.target.files![0]);
                }}
              />
              <label htmlFor={`${u.key}-file`}>
                <Button
                  variant={"outlined"}
                  component="span"
                  className={`${gStyles.secondaryButton} ${styles.button}`}
                >
                  {`${u.label} hochladen`}
                  {getKeyValue(u.key) && <CheckCircleOutlineIcon />}
                </Button>
              </label>
            </>
          );
        })}
        <Button
          type="submit"
          className={`${gStyles.primaryButton} ${styles.button}`}
          disabled={
            !getKeyValue("presentations") ||
            !getKeyValue("rooms") ||
            !getKeyValue("teachers") ||
            !getKeyValue("timeslots") ||
            !getKeyValue("locktimes")
          }
        >
          Daten Importieren
        </Button>
        <Typography variant="subtitle1" style={{ color: "red" }} gutterBottom>
          Achtung: Beim Import werden alle Daten überschrieben!
        </Typography>
      </form>
      {/* <Snackbar
        open={snackbarOpen}
        autoHideDuration={10000}
        onClose={() => {
          setSnackbarOpen(false);
          setErrorMsg("");
        }}
        message={`Fehler beim upload der Dateien: ${errorMsg}`}
      /> */}
    </>
  );
};

export default UploadFilesScreen;
