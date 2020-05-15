import React, { useState, useEffect } from "react";
import { useGStyles } from "../../theme";
import { makeStyles } from "@material-ui/core";
import PresentationTable from "./presentation/presentationTable";
import LecturerTable from "./lecturer/lecturerTable";
import LecturerContainer from "../../states/lecturerState";

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
  const [data, setData] = useState<Array<any>>([]);
  const [loadedType, setLoadedType] = useState("");

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
      setLoadedType(type);
    };

    loadData();
  }, [type]);
  const tables = {
    presentation: <PresentationTable presentations={data} />,
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
      {loadedType === type && (
        <div className={gStyles.centerFlexDiv}>
          <LecturerContainer.Provider>
            {data && getTableToRender()}
          </LecturerContainer.Provider>
        </div>
      )}
    </>
  );
};

export default EditScreen;
