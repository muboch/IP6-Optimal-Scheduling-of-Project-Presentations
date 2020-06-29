import { Button } from "@material-ui/core";
import React from "react";
import { useGStyles } from "../../../theme";
import MaterialTable from "material-table";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../../constants";
import StudentContainer from "../../../states/studentState";
import tableIcons from "../../../Helpers/tableIcons";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";

const StudentTable: React.SFC = () => {
  const gStyles = useGStyles();
  const studentStore = StudentContainer.useContainer();
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
      title: "Klasse",
      field: "schoolclass",
    },
    {
      title: "Bearbeiten",
      field: "edit",
      render: (rowData: any) => (
        <Button
          className={gStyles.primaryButton}
          onClick={() => {
            setLocation(`${SCREENROUTES.students}/edit/${rowData.id}`);
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
            studentStore.deleteById(rowData.id);
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
            title="Schüler"
            columns={columns}
            data={studentStore.students}
          />

          <div>
            <Button
              className={gStyles.primaryButton}
              onClick={() => {
                setLocation(`${SCREENROUTES.students}/edit`);
              }}
            >
              Schüler hinzufügen
            </Button>
          </div>
        </>
      </div>
    </>
  );
};

export default StudentTable;
