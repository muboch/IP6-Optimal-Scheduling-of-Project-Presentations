import React, { useState } from "react";
import { useGStyles } from "../../theme";
import PresentationTable from "./presentation/presentationTable";
import LecturerTable from "./lecturer/lecturerTable";
import LecturerContainer from "../../states/lecturerState";
import PresentationContainer from "../../states/presentationState";

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
  const [data, setData] = useState<Array<any>>([]);
  const [loadedType, setLoadedType] = useState("");

  // const loadFunction = {
  //   lecturer: loadLecturers,
  //   student: loadStudents,
  //   presentation: loadPresentations,
  // }[`${type}` as string];

  const tables = {
    presentation: <PresentationTable />,
    lecturer: <LecturerTable />,
    room: <></>,
    offtime: <></>,
    timeslot: <></>,
    Student: <></>,
  };
  const getTableToRender = () => {
    return tables[type];
  };
  return (
    <>
      <div className={gStyles.centerFlexDiv}>{getTableToRender()}</div>
    </>
  );
};

export default EditScreen;
