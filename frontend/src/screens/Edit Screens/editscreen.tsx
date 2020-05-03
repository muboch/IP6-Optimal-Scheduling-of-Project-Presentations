import React, { useState, useEffect } from "react";
import { useGStyles, theme } from "../../theme";
import PresentationEditForm from "./presentation/presentationEditForm";
import { makeStyles, Backdrop, Paper } from "@material-ui/core";
import PresentationTable from "./presentation/presentationTable";
import { Presentation } from "../../Types/types";
import { loadLecturers } from "../../Services/lecturerService";
import { loadStudents } from "../../Services/studentService";
import { loadPresentations } from "../../Services/presentationService";
import LecturerTable from "./lecturer/lecturerTable";

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
  });
  const styles = useStyles();
  const [showEditForm, setShowEditForm] = useState<boolean>(false);
  const [data, setData] = useState<Array<any>>([]);

  // const loadFunction = {
  //   lecturer: loadLecturers,
  //   student: loadStudents,
  //   presentation: loadPresentations,
  // }[`${type}` as string];

  useEffect(() => {
    setData([]);

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

    loadData();
  }, [type]);
  const tables = {
    presentation: <PresentationTable presentations={data} />,
    lecturer: <LecturerTable lecturers={data} />,
    room: <></>,
    offtime: <></>,
    timeslot: <></>,
    Student: <></>,
  };
  const getTableToRender = () => {
    return tables[type];
  };
  return (
    <div className={gStyles.centerFlexDiv}>{data && getTableToRender()}</div>
  );
};

export default EditScreen;
