import React, { useState, useEffect } from "react";
import { makeStyles, TextField, Button, Tooltip } from "@material-ui/core";
import { useGStyles } from "../../../theme";
import { Lecturer, Student, Timeslot } from "../../../Types/types";

import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";
import { SCREENROUTES } from "../../../constants";
import { useLocation } from "wouter";
import TimeslotContainer from "../../../states/timeslotState";

export interface EditFormProps {
  id?: number | undefined; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
}

const TimeslotEditForm: React.SFC<EditFormProps> = ({ id }) => {
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
  const getMaxOrderValue = (): number => {
    return Math.max(
      ...timeslotStore.timeslots.map((t) => {
        return t.sortOrder;
      })
    );
  };
  const gStyles = useGStyles();
  const styles = useStyles();
  const timeslotStore = TimeslotContainer.useContainer();
  const [timeslot, setTimeslot] = useState<Timeslot>({
    block: 1,
    priority: 50,
    date: "",
    sortOrder: getMaxOrderValue() + 1,
  });
  const [, setLocation] = useLocation();

  useEffect(() => {
    if (!timeslotStore) {
      return;
    }

    const loadDataAsync = async () => {
      if (id !== undefined) {
        setTimeslot((await timeslotStore.loadById(id))!);
      }
    };
    loadDataAsync();
  }, [id, timeslotStore]);

  const updateValue = (
    key: keyof Timeslot,
    value: string | number | Lecturer | Student | boolean | null
  ) => {
    setTimeslot({ ...timeslot!, [key]: value });
  };

  const onExitForm = () => {
    setLocation(SCREENROUTES.timeslots);
  };

  const onSaveForm = async (e: any) => {
    e.preventDefault();
    try {
      await timeslotStore.add(timeslot!);
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
        {timeslotStore.timeslots && (
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
                label="Startzeit"
                onChange={(e: any) => {
                  updateValue("date", e.currentTarget.value);
                }}
                value={timeslot.date}
                className={styles.textField50}
              ></TextField>
            </div>
            <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
              <TextField
                required
                label="Block"
                type="number"
                value={timeslot.block}
                onChange={(e: any) => {
                  updateValue("block", e.currentTarget.value);
                }}
                className={styles.textField50}
              ></TextField>
              <TextField
                required
                label="Kosten"
                type="number"
                value={timeslot.priority}
                onChange={(e: any) => {
                  updateValue("priority", e.currentTarget.value);
                }}
                className={styles.textField50}
              ></TextField>
              <TextField
                required
                label="Reihenfolge"
                type="number"
                value={timeslot.sortOrder}
                onChange={(e: any) => {
                  updateValue("sortOrder", e.currentTarget.value);
                }}
                className={styles.textField50}
              ></TextField>
            </div>
          </div>
        )}
      </form>
    </div>
  );
};

export default TimeslotEditForm;
