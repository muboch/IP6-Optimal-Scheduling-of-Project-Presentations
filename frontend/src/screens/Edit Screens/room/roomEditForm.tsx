import React, { useState, useEffect } from "react";
import {
  makeStyles,
  TextField,
  Button,
  Tooltip,
  Checkbox,
  NativeSelect,
  InputLabel,
} from "@material-ui/core";
import { useGStyles } from "../../../theme";
import { Lecturer, Student, Room } from "../../../Types/types";

import CloseIcon from "@material-ui/icons/Close";
import SaveIcon from "@material-ui/icons/Save";
import { SCREENROUTES, PRESENTATIONTYPES } from "../../../constants";
import { useLocation } from "wouter";
import RoomContainer from "../../../states/roomState";

export interface LecturerEditFormProps {
  id?: number | undefined; // Optional. If passed, we're editing an existing presentation, otherwise creating a new one
}

const RoomEditForm: React.SFC<LecturerEditFormProps> = ({ id }) => {
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
  const roomStore = RoomContainer.useContainer();
  const [room, setRoom] = useState<Room>({
    name: "",
    type: "normal",
    place: "",
    reserve: false,
  });
  const [, setLocation] = useLocation();

  useEffect(() => {
    if (!roomStore) {
      return;
    }

    const loadDataAsync = async () => {
      if (id !== undefined) {
        setRoom((await roomStore.loadById(id))!);
      }
    };
    loadDataAsync();
  }, [id, roomStore]);

  const updateValue = (
    key: keyof Room,
    value: string | number | Lecturer | Student | boolean | null
  ) => {
    setRoom({ ...room!, [key]: value });
  };

  const onExitForm = () => {
    setLocation(SCREENROUTES.rooms);
  };

  const onSaveForm = async (e: any) => {
    e.preventDefault();
    try {
      await roomStore.add(room!);
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
        {roomStore.rooms && (
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
                label="Name"
                onChange={(e: any) => {
                  updateValue("name", e.currentTarget.value);
                }}
                value={room.name}
                className={styles.textField50}
              ></TextField>
            </div>
            <div className={`${gStyles.centerFlexDiv} ${styles.centerFlexDiv}`}>
              <TextField
                required
                label="Ort"
                value={room.place}
                onChange={(e: any) => {
                  updateValue("place", e.currentTarget.value);
                }}
                className={styles.textField50}
              ></TextField>
              <InputLabel shrink htmlFor="reserve-label">
                Reserve
              </InputLabel>
              <Checkbox
                checked={room.reserve}
                onChange={(event) => {
                  updateValue("reserve", event.target.checked);
                }}
                inputProps={{ id: "reserve-label", "aria-label": "Reserve" }}
              />

              <InputLabel shrink htmlFor="age-native-label-placeholder">
                Type
              </InputLabel>
              <NativeSelect
                value={room.type || PRESENTATIONTYPES[0]}
                onChange={(e: any) => {
                  updateValue("type", e.currentTarget.value);
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
            </div>
          </div>
        )}
      </form>
    </div>
  );
};

export default RoomEditForm;
