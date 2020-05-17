import React, { useState, useEffect } from "react";
import { makeStyles, TextField, Button, Tooltip } from "@material-ui/core";
import { useGStyles } from "../../../theme";
import { Lecturer, Student } from "../../../Types/types";
import {
  _loadLecturerById,
  loadLecturers,
  _addLecturer,
} from "../../../Services/lecturerService";
import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";
import { _addPresentation } from "../../../Services/presentationService";
import LecturerContainer from "../../../states/lecturerState";

export interface LecturerEditFormProps {
  lecturerId?: number | undefined; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
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
  const lecStore = LecturerContainer.useContainer();
  const [lecturer, setLecturer] = useState<Lecturer>();
  console.log(lecturerId);

  useEffect(() => {
    if (!lecStore) {
      return;
    }

    const loadDataAsync = async () => {
      if (lecturerId !== undefined && editLecturer) {
        setLecturer(await lecStore.loadLecturerById(lecturerId));
      } else {
        setLecturer({
          email: "",
          initials: "",
          firstname: "",
          lastname: "",
        });
      }
    };

    // if (lecturerId === undefined) {
    //   // setLecturer(undefined);
    //   return;
    // }

    loadDataAsync();
  }, [lecturerId, editLecturer, lecStore]);

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
      (l) => l.email === lecturer!.email
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

  const onSaveForm = async (e: any) => {
    e.preventDefault();
    try {
      await lecStore.addLecturer(lecturer!);
      onExitForm();
    } catch (error) {}
  };

  return (
    <form onSubmit={onSaveForm}>
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
      {lecturer && lecStore.lecturers && (
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
              helperText={initialsHasError() && "Kürzel wird bereits verwendet"}
              className={styles.textField50}
            ></TextField>
          </div>
        </div>
      )}
    </form>
  );
};

export default LecturerEditForm;
