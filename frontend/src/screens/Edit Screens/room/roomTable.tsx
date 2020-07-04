import { Button } from "@material-ui/core";
import React from "react";
import { useGStyles } from "../../../theme";
import MaterialTable from "material-table";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../../constants";
import RoomContainer from "../../../states/roomState";
import tableIcons from "../../../Helpers/tableIcons";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";

export interface LecturerTableProps {}

const RoomTable: React.SFC<LecturerTableProps> = () => {
  const gStyles = useGStyles();
  const roomStore = RoomContainer.useContainer();
  const [, setLocation] = useLocation();


  const columns = [
    {
      title: "id",
      field: "id",
    },
    {
      title: "Name",
      field: "name",
    },
    {
      title: "Typ",
      field: "type",
    },
    {
      title: "Reserve",
      field: "reserve",
    },
    {
      title: "Bearbeiten",
      field: "edit",
      render: (rowData: any) => (
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            setLocation(`${SCREENROUTES.rooms}/edit/${rowData.id}`);
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
            roomStore.deleteById(rowData.id);
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
        title="Zimmer"
        columns={columns}
        data={roomStore.rooms}
      ></MaterialTable>

      <div>
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            setLocation(`${SCREENROUTES.rooms}/edit`);
          }}
        >
          Zimmer hinzufügen
        </Button>
      </div>
    </div>
  );
};

export default RoomTable;
