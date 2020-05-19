import { Offtime, Timeslot, Lecturer } from "../../../Types/types";
import { Button, Checkbox, withStyles, CheckboxProps } from "@material-ui/core";
import React from "react";
import { useGStyles, theme } from "../../../theme";
import MaterialTable from "material-table";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../../constants";
import tableIcons from "../../../Helpers/tableIcons";
import TimeslotContainer from "../../../states/timeslotState";
import LecturerContainer from "../../../states/lecturerState";
import { green } from "@material-ui/core/colors";

const OfftimeTable: React.SFC = () => {
  const lectStore = LecturerContainer.useContainer();
  const timeStore = TimeslotContainer.useContainer();
  const gStyles = useGStyles();
  const [, setLocation] = useLocation();

  const lectHasOfftime = (timeslot: Timeslot, offtimes: Array<Offtime>) => {
    const offtime = offtimes.find((ot) => ot.id === timeslot.id); // Check if the offtime exists in the array
    return offtime !== undefined; // If it does, lecturer has an offtime at timeslot
  };
  const handleChangeOfftime = (
    lect: Lecturer,
    timeslot: Timeslot,
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (event.target.checked) {
      // Add offtime
      lect.offtimes.push({
        id: timeslot.id!,
        block: timeslot.block,
        date: timeslot.date,
      });
    } else {
      lect.offtimes = lect.offtimes.filter((ot) => ot.id !== timeslot.id!);
    }
    lectStore.addLecturer(lect);
  };

  const GreenCheckbox = withStyles({
    root: {
      color: theme.palette.primary.light,
      "&$checked": {
        color: theme.palette.primary.main,
      },
    },
    checked: {},
  })((props: CheckboxProps) => <Checkbox color="default" {...props} />);

  const userColumns = [
    {
      title: "Nachname",
      field: "lastname",
    },
    {
      title: "Vorname",
      field: "firstname",
    },
  ];
  const checkboxColums = timeStore.timeslots.map((t) => {
    return {
      title: t.date.toString(),
      field: t.date.toString(),
      render: (lect: Lecturer) => (
        <GreenCheckbox
          key={`checkT${t.id}-L${lect.id}`}
          checked={lectHasOfftime(t, lect.offtimes)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            handleChangeOfftime(lect, t, event);
          }}
          name={`checkT${t.id}-L${lect.id}`}
        />
      ),
    };
  });

  const columns = [...userColumns, ...checkboxColums];
  return (
    <div
      className={gStyles.columnFlexDiv}
      style={{ maxWidth: "100%", overflowY: "auto" }}
    >
      <MaterialTable
        style={{ width: "100%", maxWidth: "1200px", minWidth: "1200px" }}
        icons={tableIcons}
        title="Dozenten"
        columns={columns}
        data={lectStore.lecturers}
      />
    </div>
  );
};

export default OfftimeTable;
