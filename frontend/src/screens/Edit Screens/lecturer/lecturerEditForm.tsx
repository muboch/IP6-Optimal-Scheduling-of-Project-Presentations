import React, { useState, useEffect } from "react";
import { makeStyles, TextField, Button, Tooltip } from "@material-ui/core";
import { useGStyles } from "../../../theme";
import { Lecturer, Student } from "../../../Types/types";
import { loadLecturerById } from "../../../Services/lecturerService";
import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";

export interface LecturerEditFormProps {
  lecturerId?: number; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
  onExitForm: () => void;
  editLecturer: boolean;
}

const LecturerEditForm: React.SFC<LecturerEditFormProps> = ({
  lecturerId,
  onExitForm,
  editLecturer,
}) => {
  const useStyles = makeStyles({
    centerFlexDiv: {
      margin: "20px",
    },
    closeButton: {
      margin: "10px",
      position: "absolute",
      top: "10px",
      right: "10px",
    },
    saveButton: {
      margin: "10px",
      position: "absolute",
      top: "60px",
      right: "10px",
    },
    textField50: {
      width: "50%",
    },
    textField80: {
      width: "80%",
    },
    textField20: {
      width: "20%",
    },
  });

  const styles = useStyles();
  const gStyles = useGStyles();
  const [lecturer, setLecturer] = useState<Lecturer>();

  const loadDataAsync = async () => {
    if (lecturerId !== undefined && editLecturer) {
      setLecturer(await loadLecturerById(lecturerId));
    }
  };

  useEffect(() => {
    console.log("loadDataAsync");

    loadDataAsync();
  }, [lecturerId]);

  const updateLecturerValue = (
    key: keyof Lecturer,
    value: string | number | Lecturer | Student | null
  ) => {
    setLecturer({ ...lecturer!, [key]: value });
  };

  const onSaveForm = () => {};

  return (
    <form>
      <Tooltip title="Abbrechen und Schliessen">
        <Button
          className={`${gStyles.secondaryButton} ${styles.closeButton}`}
          onClick={onExitForm}
        >
          <CloseIcon />
        </Button>
      </Tooltip>
      <Tooltip title="Speichern">
        <Button
          type="submit"
          className={`${gStyles.primaryButton} ${styles.saveButton}`}
          onClick={onSaveForm}
        >
          <SaveIcon />
        </Button>
      </Tooltip>
      {lecturer && (
        <div className={gStyles.columnFlexDiv}>
          <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
            <TextField
              className={styles.textField50}
              required
              label="ID"
              type="number"
              disabled
              value={lecturerId}
              InputLabelProps={{ shrink: true }}
            ></TextField>
            <TextField
              required
              label="Vorname"
              onChange={(e: any) => {
                updateLecturerValue("firstname", e.currentTarget.value);
              }}
              value={lecturer.firstname}
              className={styles.textField50}
            ></TextField>
          </div>
          <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
            <TextField
              required
              label="Nachname"
              value={lecturer.lastname}
              onChange={(e: any) => {
                updateLecturerValue("lastname", e.currentTarget.value);
              }}
              className={styles.textField50}
            ></TextField>
            <TextField
              required
              label="Email"
              value={lecturer.lastname}
              onChange={(e: any) => {
                updateLecturerValue("email", e.currentTarget.value);
              }}
              className={styles.textField50}
            ></TextField>
          </div>
          <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
            <TextField
              required
              label="Kürzel"
              value={lecturer.lastname}
              onChange={(e: any) => {
                updateLecturerValue("initials", e.currentTarget.value);
              }}
              className={styles.textField50}
            ></TextField>
          </div>
        </div>
      )}
    </form>
  );
};

export default LecturerEditForm;
