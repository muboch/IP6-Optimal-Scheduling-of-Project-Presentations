import React, { useState, useEffect } from "react";
import {
  Backdrop,
  makeStyles,
  CircularProgress,
  TextField,
  Paper,
  Button,
} from "@material-ui/core";
import { useGStyles, theme } from "../../theme";
import { Presentation, Lecturer, Student } from "../../Types/types";
import Autocomplete from "@material-ui/lab/Autocomplete";
import { loadLecturers } from "../../Services/lecturerService";
import { loadStudents } from "../../Services/studentService";
import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";
import { loadPresentationById } from "../../Services/presentationService";

export interface PresentationEditFormProps {
  presentationId?: number; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
  onExitForm: () => void;
  editPresentation: boolean;
}

const PresentationEditForm: React.SFC<PresentationEditFormProps> = ({
  presentationId,
  onExitForm,
  editPresentation,
}) => {
  const useStyles = makeStyles({
    centerFlexDiv: {
      margin: "10px",
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
  });

  const styles = useStyles();
  const gStyles = useGStyles();
  const [lecturers, setLecturers] = useState<Array<Lecturer>>([]);
  const [students, setStudents] = useState<Array<Student>>([]);
  const [presentation, setPresentation] = useState<Presentation>();

  const loadDataAsync = async () => {
    setLecturers(await loadLecturers());
    setStudents(await loadStudents());
    if (presentationId !== undefined && editPresentation) {
      setPresentation(await loadPresentationById(presentationId));
    }
  };

  useEffect(() => {
    console.log("loadDataAsync");

    loadDataAsync();
  }, [presentationId]);

  const updatePresentationValue = (
    key: keyof Presentation,
    value: string | number | Lecturer | Student | null
  ) => {
    setPresentation({ ...presentation!, [key]: value });
  };

  const onSaveForm = () => {};

  return (
    <form>
      <Button
        className={`${gStyles.secondaryButton} ${styles.closeButton}`}
        onClick={onExitForm}
      >
        <CloseIcon />
      </Button>
      <Button
        type="submit"
        className={`${gStyles.primaryButton} ${styles.saveButton}`}
        onClick={onSaveForm}
      >
        <SaveIcon />
      </Button>
      {presentation && students && lecturers && (
        <div className={gStyles.columnFlexDiv}>
          <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
            <TextField
              required
              label="ID"
              type="number"
              disabled
              value={presentationId}
              InputLabelProps={{ shrink: true }}
            ></TextField>
            <TextField
              required
              label="Nr"
              onChange={(e: any) => {
                updatePresentationValue("nr", e.currentTarget.value);
              }}
              value={presentation.nr}
            ></TextField>

            <TextField
              required
              label="Titel"
              value={presentation.title}
              onChange={(e: any) => {
                updatePresentationValue("title", e.currentTarget.value);
              }}
            ></TextField>
          </div>
          <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
            <Autocomplete
              id="combo-box-demo"
              options={lecturers}
              getOptionLabel={(lecturer: Lecturer) =>
                `${lecturer.lastname}, ${lecturer.firstname}`
              }
              style={{ width: 300 }}
              value={presentation?.coach}
              onChange={(_: any, newValue: Lecturer | null) => {
                updatePresentationValue("coach", newValue);
              }}
              renderInput={(params) => (
                <TextField {...params} label="Coach" variant="outlined" />
              )}
            />

            <Autocomplete
              id="combo-box-demo"
              options={lecturers}
              getOptionLabel={(lecturer: Lecturer) =>
                `${lecturer.lastname}, ${lecturer.firstname}`
              }
              style={{ width: 300 }}
              value={presentation.expert}
              renderInput={(params) => (
                <TextField {...params} label="Expert" variant="outlined" />
              )}
              onChange={(_: any, newValue: Lecturer | null) => {
                updatePresentationValue("expert", newValue);
              }}
            />
          </div>
          <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
            <Autocomplete
              id="combo-box-demo"
              options={students}
              getOptionLabel={(student: Student) => {
                return student.name;
              }}
              style={{ width: 300 }}
              value={presentation.studentOne}
              renderInput={(params) => (
                <TextField {...params} label="Schüler 1" variant="outlined" />
              )}
              onChange={(_: any, newValue: Student | null) => {
                updatePresentationValue("studentOne", newValue);
              }}
            />
            <Autocomplete
              id="combo-box-demo"
              options={students}
              getOptionLabel={(student: Student) => {
                return student.name;
              }}
              style={{ width: 300 }}
              value={presentation.studentTwo}
              renderInput={(params) => (
                <TextField {...params} label="Schüler 2" variant="outlined" />
              )}
              onChange={(_: any, newValue: Student | null) => {
                updatePresentationValue("studentTwo", newValue);
              }}
            />
          </div>
        </div>
      )}
    </form>
  );
};

export default PresentationEditForm;
