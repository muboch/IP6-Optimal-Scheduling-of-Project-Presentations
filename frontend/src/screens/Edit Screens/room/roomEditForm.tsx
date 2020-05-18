import React, { useState, useEffect } from "react";
import { makeStyles, TextField, Button, Tooltip } from "@material-ui/core";
import { useGStyles } from "../../../theme";
import { Lecturer, Student } from "../../../Types/types";

import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";
import LecturerContainer from "../../../states/lecturerState";
import { SCREENROUTES } from "../../../constants";
import { useLocation } from "wouter";

export interface LecturerEditFormProps {
  id?: number | undefined; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
}

const RoomEditForm: React.SFC<LecturerEditFormProps> = ({ id }) => {
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

  const gStyles = useGStyles();
  const styles = useStyles();
  const lecStore = LecturerContainer.useContainer();
  const [lecturer, setLecturer] = useState<Lecturer>({
    email: "",
    initials: "",
    firstname: "",
    lastname: "",
  });
  const [, setLocation] = useLocation();

  useEffect(() => {
    if (!lecStore) {
      return;
    }

    const loadDataAsync = async () => {
      if (id !== undefined) {
        setLecturer((await lecStore.loadLecturerById(id))!);
      }
    };
    loadDataAsync();
  }, [id, lecStore]);

  const updateLecturerValue = (
    key: keyof Lecturer,
    value: string | number | Lecturer | Student | null
  ) => {
    setLecturer({ ...lecturer!, [key]: value });
  };

  const indexOfAllInitials = (
    arr: Array<Lecturer>,
    val: Lecturer
  ): Array<number> =>
    arr.reduce(
      (acc: Array<number>, el, i) =>
        el.initials === val.initials ? [...acc, i] : acc,
      []
    );

  const initialsHasError = () => {
    const indexes: Array<number> = indexOfAllInitials(
      lecStore.lecturers,
      lecturer!
    );
    const indexOfLecturer = lecStore.lecturers.findIndex(
      (l) => l.id === lecturer!.id
    );
    console.log("indexes: ", indexes);
    console.log("lecindex: ", indexOfLecturer);

    if (indexes.length > 1) {
      return true;
    }
    if (indexes.length > 0 && !indexes.includes(indexOfLecturer)) {
      return true;
    }
    return false;
  };
  const onExitForm = () => {
    setLocation(SCREENROUTES.lecturers);
  };

  const onSaveForm = async (e: any) => {
    e.preventDefault();
    try {
      await lecStore.addLecturer(lecturer!);
      onExitForm();
    } catch (error) {}
  };

  return (
    <div className={gStyles.centerFlexDiv}>
      <form onSubmit={onSaveForm} style={{ maxWidth: "1200px", width: "100%" }}>
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
          >
            <SaveIcon />
          </Button>
        </Tooltip>
        {lecStore.lecturers && (
          <div className={gStyles.columnFlexDiv}>
            <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
              <TextField
                className={styles.textField50}
                required
                label="ID"
                type="number"
                disabled
                value={id}
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
                type="email"
                label="Email"
                value={lecturer.email}
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
                value={lecturer.initials}
                onChange={(e: any) => {
                  updateLecturerValue("initials", e.currentTarget.value);
                }}
                error={initialsHasError()}
                helperText={
                  initialsHasError() && "Kürzel wird bereits verwendet"
                }
                className={styles.textField50}
              ></TextField>
            </div>
          </div>
        )}
      </form>
    </div>
  );
};

export default RoomEditForm;
