import { Lecturer } from "../../../Types/types";
import { Button } from "@material-ui/core";
import React, { useState, useEffect, forwardRef } from "react";
import { makeStyles } from "@material-ui/styles";
import { useGStyles } from "../../../theme";
import { Order } from "../../../Helpers/helpers";
import MaterialTable from "material-table";
import TableSortLabel from "@material-ui/core/TableSortLabel";
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
  const [rows, setRows] = useState<Array<roomRow>>([]);
  const [roomToEdit, setRoomToEdit] = useState<Lecturer>();
  const [location, setLocation] = useLocation();

  type roomRow = {
    name: string;
    id: string;
    type: string;
    place: string;
    reserve: boolean;
  };

  // useEffect(() => {
  //   const loadRows = async () => {
  //     const rows: Array<roomRow> = roomStore.rooms.map((r) => {
  //       return {
  //         id: r.id!,
  //         name: r.name,
  //         place: r.place,
  //         type: r.type,
  //         reserve: r.reserve,
  //       };
  //     });
  //     setRows(rows);
  //   };
  //   loadRows();
  // }, [lecStore.lecturers]);

  const columns = [
    {
      title: "Name",
      field: "name",
    },
    {
      title: "id",
      field: "id",
    },
    {
      title: "Typ",
      field: "type",
    },
    {
      title: "Ort",
      field: "place",
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
    <>
      <div className={gStyles.columnFlexDiv}>
        {roomToEdit === undefined && (
          <>
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
          </>
        )}
      </div>
    </>
  );
};

export default RoomTable;
