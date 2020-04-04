import React, { useState, FormEvent } from "react";
import { Button, makeStyles } from "@material-ui/core";
import { useGStyles } from "../../theme";
import CheckCircleOutlineIcon from "@material-ui/icons/CheckCircleOutline";

const useStyles = makeStyles(theme => ({
  input: {
    display: "none"
  },
  button: {
    width: "400px"
  }
}));

const PlanningScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const styles = useStyles();
  const [files, setFiles] = useState<Files>({
    presentations: undefined,
    rooms: undefined,
    timeslots: undefined,
    teachers: undefined
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
  };

  const uploadInfos: Array<UploadInfo> = [
    { key: "presentations", label: "Präsentationen" },
    { key: "rooms", label: "Räume" },
    { key: "timeslots", label: "Zeitslots" },
    { key: "teachers", label: "Lehrpersonen" }
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

    try {
      const res = await fetch(`${process.env.API_ENDPOINT}/api/plannings`, {
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
                className={`${gStyles.secondaryButton} ${styles.button}`}
              >
                {`${u.label} hochladen`}
                {getKeyValue(u.key) && (
                  <CheckCircleOutlineIcon/>
                )}
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
          !getKeyValue("timeslots")
        }
      >
        Planung erstellen
      </Button>
    </form>
  );
};

export default PlanningScreen;
