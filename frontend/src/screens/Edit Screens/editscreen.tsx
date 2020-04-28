import React, { useState, useEffect } from "react";
import { useGStyles, theme } from "../../theme";
import PresentationEditForm from "./presentationEditForm";
import { makeStyles, Backdrop, Paper } from "@material-ui/core";
import PresentationTable from "./presentationTable";
import { Presentation } from "../../Types/types";
import { loadLecturers } from "../../Services/lecturerService";
import { loadStudents } from "../../Services/studentService";
import { loadPresentations } from "../../Services/presentationService";

export interface EditScreenProps {
  type:
    | "presentation"
    | "room"
    | "lecturer"
    | "offtime"
    | "timeslot"
    | "Student";
}

const EditScreen: React.SFC<EditScreenProps> = ({ type }) => {
  const gStyles = useGStyles();
  const useStyles = makeStyles({
    table: {
      minWidth: 650,
      maxWidth: 1000,
    },
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: "#fff",
    },
    paper: {
      minWidth: 650,
      maxWidth: 1000,
      minHeight: 650,
      maxHeight: 1000,
    },
  });
  const styles = useStyles();
  const [showEditForm, setShowEditForm] = useState<boolean>(false);
  const [presentationToEdit, setPresentationToEdit] = useState<number>();
  const [data, setData] = useState<Array<Presentation>>([]);

  // const loadFunction = {
  //   lecturer: loadLecturers,
  //   student: loadStudents,
  //   presentation: loadPresentations,
  // }[`${type}` as string];

  const loadData = async () => {
    try {
      const res = await fetch(
        `${process.env.REACT_APP_API_ENDPOINT}/api/${type}`
      );
      const json = await res.json();
      console.log(json);
      if (res.ok) {
        setData(json);
      }
    } catch (Error) {}
  };

  useEffect(() => {
    loadData();
  }, []);
  const tables = { PresentationTable };

  return (
    <div className={gStyles.centerFlexDiv}>
      <PresentationTable
        presentations={data}
        setPresentationToEdit={setPresentationToEdit}
      />
      <Backdrop
        className={styles.backdrop}
        open={presentationToEdit !== undefined}
        //onClick={() => setPresentationToEdit(undefined)}
      >
        <Paper className={styles.paper}>
          <PresentationEditForm
            onExitForm={() => setPresentationToEdit(undefined)}
            presentationId={presentationToEdit}
            editPresentation={
              presentationToEdit! < data.length
            }
          ></PresentationEditForm>
        </Paper>
      </Backdrop>
    </div>
  );
};

export default EditScreen;
