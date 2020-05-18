import { Presentation } from "../../../Types/types";
import { useGStyles } from "../../../theme";
import { Button } from "@material-ui/core";
import MaterialTable from "material-table";
import React, { useState, useEffect } from "react";
import { makeStyles } from "@material-ui/styles";
import { Order } from "../../../Helpers/helpers";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import PresentationContainer from "../../../states/presentationState";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../../constants";
import tableIcons from "../../../Helpers/tableIcons";

export interface PresentationTableProps {}

const PresentationTable: React.SFC<PresentationTableProps> = () => {
  const useStyles = makeStyles({
    table: {
      minWidth: 900,
      maxWidth: 1200,
    },
  });
  const gStyles = useGStyles();

  const [] = React.useState<Order>("asc");
  const [] = React.useState<keyof presentationRow>("id");
  const [, setPage] = React.useState(0);
  const [, setRowsPerPage] = React.useState(5);
  const [rows, setRows] = useState<Array<presentationRow>>([]);
  const presStore = PresentationContainer.useContainer();
  const [, setLocation] = useLocation();

  type presentationRow = {
    id: number;
    title: string;
    coach: string;
    expert: string;
    studentOne: string;
    studentTwo: string;
    type: string;
    nr: string;
  };

  useEffect(() => {
    const loadRows = () => {
      const rows: Array<presentationRow> = presStore.presentations.map(
        (p: Presentation) => {
          return {
            id: p.id!,
            title: p.title,
            studentOne: p.studentOne!.name,
            studentTwo: p.studentTwo ? p.studentTwo?.name : "",
            coach: `${p.coach!.lastname}, ${p.coach!.firstname}`,
            expert: `${p.expert!.lastname}, ${p.expert!.firstname}`,
            type: p.type as string,
            nr: p.nr,
          };
        }
      );
      setRows(rows);
    };
    loadRows();
  }, [presStore.presentations]);

  const columns = [
    {
      title: "id",
      field: "id",
    },
    {
      title: "nummer",
      field: "nr",
    },
    {
      title: "Titel",
      field: "title",
    },
    {
      title: "Dozent",
      field: "coach",
    },
    {
      title: "Experte",
      field: "expert",
    },
    {
      title: "Schüler 1",
      field: "studentOne",
    },
    {
      title: "Schüler 2",
      field: "studentTwo",
    },
    {
      title: "Typ",
      field: "type",
    },
    {
      title: "Bearbeiten",
      field: "edit",
      render: (rowData: any) => (
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            setLocation(`${SCREENROUTES.presentations}/edit/${rowData.id}`);
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
            presStore.deletePresentationById(rowData.id);
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
        <>
          <MaterialTable
            style={{ width: "100%", minWidth: "1200px" }}
            icons={tableIcons}
            title="Präsentationen"
            columns={columns}
            data={rows}
          ></MaterialTable>

          <Button
            className={gStyles.primaryButton}
            onClick={() => setLocation(`${SCREENROUTES.presentations}/edit`)}
          >
            Präsentation Hinzufügen
          </Button>
        </>
      </div>
    </>
  );
};

export default PresentationTable;
