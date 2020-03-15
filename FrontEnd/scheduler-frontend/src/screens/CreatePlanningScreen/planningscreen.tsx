import React, { useState, FormEvent } from "react";
import { Button, makeStyles } from "@material-ui/core";
import { useGStyles } from "../../theme";
import { API } from "../../constants";

const useStyles = makeStyles(theme => ({
  input: {
    display: "none"
  }
}));

const PlanningScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const styles = useStyles();
  const [files, setFiles] = useState({});

  type UploadInfo = {
    key: string;
    label: string;
  };

  const uploadInfos: Array<UploadInfo> = [
    { key: "presentations", label: "Präsentationen" },
    { key: "rooms", label: "Räume" },
    { key: "timeslots", label: "Zeitslots" },
    { key: "teachers", label: "Lehrpersonen" }
  ];
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

    try {
      const res = await fetch(API.endpoint, {
        // content-type header should not be specified!
        method: "POST",
        body: formData
      });
      const json = await res.json();
    } catch (error) {
      console.log("error", error);
      return;
    }
    setFiles({});
  };

  return (
    <form
      className={gStyles.columnFlexDiv}
      onSubmit={(e: FormEvent<HTMLFormElement>) => uploadFiles(e)}
    >
      {uploadInfos.map(u => {
        return (
          <>
            <input
              accept=".csv"
              className={styles.input}
              id={`${u.key}-file`}
              type="file"
              onChange={e => {
                setFileForKey(u.key, e.target.files![0]);
              }}
            />
            <label htmlFor={`${u.key}-file`}>
              <Button
                variant={"outlined"}
                component="span"
                className={gStyles.secondaryButton}
              >
                {`${u.label} hochladen`}
              </Button>
            </label>
          </>
        );
      })}
      <Button
        type="submit"
        className={gStyles.primaryButton}
        disabled={Object.entries(files).length !== 4}
      >
        Planung erstellen
      </Button>
    </form>
  );
};

export default PlanningScreen;
