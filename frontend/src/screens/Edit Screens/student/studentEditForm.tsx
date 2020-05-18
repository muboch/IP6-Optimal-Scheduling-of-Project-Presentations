import React, { useState, useEffect } from "react";
import { makeStyles, TextField, Button, Tooltip } from "@material-ui/core";
import { useGStyles } from "../../../theme";
import { Lecturer, Student } from "../../../Types/types";

import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";
import { SCREENROUTES } from "../../../constants";
import { useLocation } from "wouter";
import StudentContainer from "../../../states/studentState";

export interface EditFormProps {
  id?: number | undefined; // Optional. If passed, we're editing an existing item, otherwise creating a new one
}

const StudentEditForm: React.SFC<EditFormProps> = ({ id }) => {
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
  const studentStore = StudentContainer.useContainer();
  const [student, setStudent] = useState<Student>({
    class: "",
    name: "",
  });
  const [, setLocation] = useLocation();

  useEffect(() => {
    if (!studentStore) {
      return;
    }

    const loadDataAsync = async () => {
      if (id !== undefined) {
        setStudent((await studentStore.loadById(id))!);
      }
    };
    loadDataAsync();
  }, [id, studentStore]);

  const updateValue = (
    key: keyof Student,
    value: string | number | Lecturer | Student | boolean | null
  ) => {
    setStudent({ ...student!, [key]: value });
  };

  const onExitForm = () => {
    setLocation(SCREENROUTES.students);
  };

  const onSaveForm = async (e: any) => {
    e.preventDefault();
    try {
      await studentStore.add(student!);
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
              label="Name"
              onChange={(e: any) => {
                updateValue("name", e.currentTarget.value);
              }}
              value={student.name}
              className={styles.textField50}
            ></TextField>
            <TextField
              required
              label="Klasse"
              value={student.class}
              onChange={(e: any) => {
                updateValue("class", e.currentTarget.value);
              }}
              className={styles.textField50}
            ></TextField>
          </div>
        </div>
      </form>
    </div>
  );
};

export default StudentEditForm;
