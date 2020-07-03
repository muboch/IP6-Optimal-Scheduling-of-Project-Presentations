import React, { useState, useEffect } from "react";
import { useGStyles } from "../../../theme";
import {
  makeStyles,
  TextField,
  Button,
  FormControl,
  InputLabel,
  NativeSelect,
  Tooltip,
} from "@material-ui/core";
import { Presentation, Lecturer, Student } from "../../../Types/types";
import Autocomplete from "@material-ui/lab/Autocomplete";
import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";
import { PRESENTATIONTYPES, SCREENROUTES } from "../../../constants";
import PresentationContainer from "../../../states/presentationState";
import LecturerContainer from "../../../states/lecturerState";
import { loadStudents } from "../../../Services/studentService";
import { useLocation } from "wouter";

export interface PresentationEditFormProps {
  id?: number | undefined; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
}

const PresentationEditForm: React.SFC<PresentationEditFormProps> = ({ id }) => {
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
  const [students, setStudents] = useState<Array<Student>>([]);
  const [presentation, setPresentation] = useState<Presentation>({
    id: undefined,
    title: "",
    type: "normal",
  });
  const [, setLocation] = useLocation();

  const presStore = PresentationContainer.useContainer();
  const lectStore = LecturerContainer.useContainer();

  useEffect(() => {
    if (!presStore) {
      return;
    }

    const loadDataAsync = async () => {
      setStudents(await loadStudents());

      if (id !== undefined) {
        setPresentation((await presStore.loadPresentationById(id))!);
      }
    };
    console.log("loadDataAsync");

    loadDataAsync();
  }, [id, presStore]);

  const updatePresentationValue = (
    key: keyof Presentation,
    value: string | number | Lecturer | Student | null
  ) => {
    setPresentation({ ...presentation!, [key]: value });
  };
  const onExitForm = () => {
    console.log("exitForm");

    setLocation(SCREENROUTES.presentations);
  };

  const onSaveForm = async (e: any) => {
    e.preventDefault();
    try {
      await presStore.addPresentation(presentation!);
      onExitForm();
    } catch (error) {
      console.log("error");
    }
  };

  const studentHasError = (student?: Student) => {
    if (!student) {
      return false;
    }
    const presentationsForStudent = presStore.presentations
      .filter(
        (p) =>
          p.studentOne?.id === student.id || p.studentTwo?.id === student.id
        // p.studentOne?.id === presentation?.studentTwo?.id
      ) // Get all presentations where studentOne is assigned
      .filter((p) => p.id !== presentation?.id); // Remove current presentation from array

    return (
      presentation!.studentOne?.id === presentation!.studentTwo?.id || // StudentOne and StudentTwo are the same
      presentationsForStudent.length > 0 // Too many presentations for student
    );
  };

  return (
    <div className={gStyles.centerFlexDiv}>
      <form style={{ maxWidth: "1200px", width: "100%" }}>
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
        {presentation && students && lectStore.lecturers && (
          <div className={gStyles.columnFlexDiv} style={{ width: "100%" }}>
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
                label="Titel"
                value={presentation.title || ""}
                onChange={(e: any) => {
                  updatePresentationValue("title", e.currentTarget.value);
                }}
                className={styles.textField80}
              ></TextField>
            </div>
            <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
              <FormControl className={styles.textField20}>
                <InputLabel shrink htmlFor="age-native-label-placeholder">
                  Type
                </InputLabel>
                <NativeSelect
                  value={presentation.type || PRESENTATIONTYPES[0]}
                  onChange={(e: any) => {
                    updatePresentationValue("type", e.currentTarget.value);
                  }}
                  inputProps={{
                    name: "Type",
                    id: "age-native-label-placeholder",
                  }}
                >
                  {PRESENTATIONTYPES.map((p) => {
                    return <option value={p}>{p}</option>;
                  })}
                </NativeSelect>
              </FormControl>
            </div>
            <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
              <Autocomplete
                className={styles.textField50}
                id="combo-box-demo"
                options={lectStore.lecturers}
                getOptionLabel={(lecturer: Lecturer) =>
                  `${lecturer.lastname}, ${lecturer.firstname}`
                }
                value={presentation?.coach || null}
                onChange={(_: any, newValue: Lecturer | null) => {
                  updatePresentationValue("coach", newValue);
                }}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    error={
                      presentation.coach &&
                      presentation.coach?.id === presentation.expert?.id
                    }
                    label="Coach"
                    variant="outlined"
                  />
                )}
              />

              <Autocomplete
                className={styles.textField50}
                id="combo-box-demo"
                options={lectStore.lecturers}
                getOptionLabel={(lecturer: Lecturer) =>
                  `${lecturer.lastname}, ${lecturer.firstname}`
                }
                value={presentation.expert || null}
                renderInput={(params) => (
                  <TextField
                    required
                    {...params}
                    error={
                      presentation.expert &&
                      presentation.coach?.id === presentation.expert?.id
                    }
                    label="Expert"
                    variant="outlined"
                  />
                )}
                onChange={(_: any, newValue: Lecturer | null) => {
                  updatePresentationValue("expert", newValue);
                }}
              />
            </div>
            <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
              <Autocomplete
                className={styles.textField50}
                id="combo-box-s1"
                options={students}
                getOptionLabel={(student: Student) => {
                  return student.name;
                }}
                value={presentation.studentOne || null}
                renderInput={(params) => (
                  <TextField
                    required
                    {...params}
                    error={studentHasError(presentation.studentOne!)}
                    label="Schüler 1"
                    variant="outlined"
                    helperText={
                      studentHasError(presentation.studentOne!) &&
                      "Ein Schüler darf nur einer Präsentation zugewiesen werden"
                    }
                  />
                )}
                onChange={(_: any, newValue: Student | null) => {
                  updatePresentationValue("studentOne", newValue);
                }}
              />
              <Autocomplete
                id="combo-box-s2"
                className={styles.textField50}
                options={students}
                getOptionLabel={(student: Student) => {
                  return student.name;
                }}
                value={presentation.studentTwo || null}
                renderInput={(params) => (
                  <TextField
                    error={studentHasError(presentation.studentTwo!)}
                    helperText={
                      studentHasError(presentation.studentTwo!) &&
                      "Ein Schüler darf nur einer Präsentation zugewiesen werden"
                    }
                    {...params}
                    label="Schüler 2"
                    variant="outlined"
                  />
                )}
                onChange={(_: any, newValue: Student | null) => {
                  updatePresentationValue("studentTwo", newValue);
                }}
              />
            </div>
          </div>
        )}
      </form>
    </div>
  );
};

export default PresentationEditForm;
