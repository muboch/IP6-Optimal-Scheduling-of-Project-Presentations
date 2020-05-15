import MessageContainer from "../states/messageState";
import { useEffect, useState } from "react";
import { Snackbar } from "@material-ui/core";
import React from "react";

export interface NotifierProps {}

const Notifier: React.SFC<NotifierProps> = () => {
  const msgStore = MessageContainer.useContainer();
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  useEffect(() => {
    if (msgStore.message === "") {
      return;
    }
    setSnackbarOpen(true);
  }, [msgStore.message]);

  return (
    <Snackbar
      open={snackbarOpen}
      autoHideDuration={4000}
      onClose={() => {
        setSnackbarOpen(false);
        msgStore.setMessage("");
      }}
      message={msgStore.message}
    />
  );
};

export default Notifier;
