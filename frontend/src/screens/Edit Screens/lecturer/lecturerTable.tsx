import { Lecturer } from "../../../Types/types";
import { Button } from "@material-ui/core";
import React, { useState } from "react";
import { makeStyles } from "@material-ui/styles";
import { useGStyles } from "../../../theme";
import EditIcon from "@material-ui/icons/Edit";
import MaterialTable from "material-table";
import DeleteIcon from "@material-ui/icons/Delete";
import { Order } from "../../../Helpers/helpers";

import MessageContainer from "../../../states/messageState";
import LecturerContainer from "../../../states/lecturerState";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../../constants";
import tableIcons from "../../../Helpers/tableIcons";

export interface LecturerTableProps {}

const LecturerTable: React.SFC<LecturerTableProps> = () => {
  const useStyles = makeStyles({
    table: {
      minWidth: 900,
      maxWidth: 1200,
    },
  });
  const lectStore = LecturerContainer.useContainer();
  const gStyles = useGStyles();
  const [, setLocation] = useLocation();

  const columns = [
    {
      title: "id",
      field: "id",
    },
    {
      title: "Nachname",
      field: "lastname",
    },
    {
      title: "Vorname",
      field: "firstname",
    },
    {
      title: "Email",
      field: "email",
    },
    {
      title: "Kürzel",
      field: "initials",
    },
    {
      title: "Bearbeiten",
      field: "edit",
      render: (rowData: any) => (
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            setLocation(`${SCREENROUTES.lecturers}/edit/${rowData.id}`);
          }}
        >
          <EditIcon />
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
            lectStore.deleteLecturerById(rowData.id);
          }}
        >
          <DeleteIcon />
        </Button>
      ),
    },
  ];

  return (
    <>
      <div className={gStyles.columnFlexDiv}>
        <>
          <MaterialTable
            style={{ width: "100%", minWidth: "1200px" }}
            icons={tableIcons}
            title="Dozenten"
            columns={columns}
            data={lectStore.lecturers}
          />
          <div>
            <Button
              className={gStyles.primaryButton}
              onClick={() => {
                setLocation(`${SCREENROUTES.lecturers}/edit`);
              }}
            >
              Dozent Hinzufügen
            </Button>
          </div>
        </>
      </div>
    </>
  );
};

export default LecturerTable;
