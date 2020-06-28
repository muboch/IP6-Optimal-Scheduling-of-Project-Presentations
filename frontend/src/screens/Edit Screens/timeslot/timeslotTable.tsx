import { Button } from "@material-ui/core";
import React from "react";
import { useGStyles } from "../../../theme";
import MaterialTable from "material-table";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../../constants";
import tableIcons from "../../../Helpers/tableIcons";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import TimeslotContainer from "../../../states/timeslotState";

const TimeslotTable: React.SFC = () => {
  const gStyles = useGStyles();
  const timeslotStore = TimeslotContainer.useContainer();
  const [, setLocation] = useLocation();

  const columns = [
    {
      title: "id",
      field: "id",
    },
    {
      title: "Reihenfolge",
      field: "sortOrder",
      defaultSort: "asc" as "asc" | "desc", // dirty type-hack because the compiler doesn't recognize strings
    },
    {
      title: "Startzeit",
      field: "date",
    },
    {
      title: "Block",
      field: "block",
    },
    {
      title: "Kosten",
      field: "priority",
    },

    {
      title: "Bearbeiten",
      field: "edit",
      render: (rowData: any) => (
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            setLocation(`${SCREENROUTES.timeslots}/edit/${rowData.id}`);
          }}
        >
          <EditIcon></EditIcon>
        </Button>
      ),
    },
    {
      title: "Löschen",
      field: "delete",
      render: (rowData: any) => (
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            timeslotStore.deleteById(rowData.id);
          }}
        >
          <DeleteIcon></DeleteIcon>
        </Button>
      ),
    },
  ];

  return (
    <div className={gStyles.columnFlexDiv}>
      <MaterialTable
        style={{ width: "100%", minWidth: "1200px" }}
        icons={tableIcons}
        title="Zeitslots"
        columns={columns}
        data={timeslotStore.timeslots}
      ></MaterialTable>

      <div>
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            setLocation(`${SCREENROUTES.timeslots}/edit`);
          }}
        >
          Zeitslot hinzufügen
        </Button>
      </div>
    </div>
  );
};

export default TimeslotTable;
