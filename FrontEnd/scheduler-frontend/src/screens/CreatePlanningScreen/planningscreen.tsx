import React, { useState, FormEvent } from "react";
import { Button, makeStyles } from "@material-ui/core";
import { useGStyles } from "../../theme";

const useStyles = makeStyles(theme => ({
  input: {
    display: "none"
  }
}));

const PlanningScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const styles = useStyles();
  const [files, setFiles] = useState();

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

  const uploadFiles = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append("file", file);
    const config = {
      headers: {
        "content-type": "multipart/form-data"
      }
    };
    return post(url, formData, config);

    console.log("uploadfiles");
  };

  return (
    <form className={gStyles.columnFlexDiv} onSubmit={e => uploadFiles(e)}>
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
              <Button variant={"outlined"} component="span" className={gStyles.secondaryButton}>
                {`${u.label} hochladen`}
              </Button>
            </label>
          </>
        );
      })}
      <Button type="submit" className={gStyles.primaryButton}>
        Planung erstellen
      </Button>
    </form>
  );
};

export default PlanningScreen;
