import React, { useState, useEffect } from "react";
import { useGStyles, theme } from "../../theme";
import PresentationEditForm from "./presentationEditForm";
import { makeStyles, Backdrop } from "@material-ui/core";

export interface EditScreenProps {
  type: "presentations" | "rooms" | "lecturers" | "offtimes" | "timeslots";
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
  });
  const styles = useStyles();
  const [showEditForm, setShowEditForm] = useState<boolean>(false);
  const [data, setData] = useState(undefined);

  const loadData = async () => {
    try {
      const res = await fetch(
        `${process.env.REACT_APP_API_ENDPOINT}/api/${type}`
      );
      const json = await res.json();
      console.log(json);
      setData(json);
    } catch (Error) {}
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <div className={gStyles.centerFlexDiv}>
      <div>Table for data</div>
      <Backdrop
        className={styles.backdrop}
        open={showEditForm}
        onClick={() => setShowEditForm(false)}
      >
        <PresentationEditForm></PresentationEditForm>
      </Backdrop>
    </div>
  );
};

export default EditScreen;
