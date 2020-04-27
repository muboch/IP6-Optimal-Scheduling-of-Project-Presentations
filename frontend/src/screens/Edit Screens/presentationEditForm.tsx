import React, { useState } from "react";
import { Backdrop, makeStyles, CircularProgress, TextField } from "@material-ui/core";
import { useGStyles, theme } from "../../theme";
import { Presentations } from "../../Types/types";

export interface PresentationEditFormProps {
  presentationId?: number; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
}

const PresentationEditForm: React.SFC<PresentationEditFormProps> = () => {
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
  const gStyles = useGStyles();




  return (
    <form>
      <div className={gStyles.centerFlexDiv}>
        <TextField required label="Titel" ></TextField>
        <TextField required label="Titel" ></TextField>


      </div>
    </form>
  );
};

export default PresentationEditForm;
