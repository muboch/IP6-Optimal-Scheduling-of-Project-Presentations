import { Presentation } from "../../../Types/types";
import {
  TableContainer,
  Paper,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Button,
  Typography,
  TablePagination,
  Backdrop,
} from "@material-ui/core";
import React, { useState, useEffect } from "react";
import { makeStyles } from "@material-ui/styles";
import { useGStyles } from "../../../theme";
import { stableSort, getComparator, Order } from "../../../Helpers/helpers";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import PresentationEditForm from "./presentationEditForm";
import PresentationContainer from "../../../states/presentationState";
import LecturerContainer from "../../../states/lecturerState";

export interface PresentationTableProps {}

const PresentationTable: React.SFC<PresentationTableProps> = ({}) => {
  const useStyles = makeStyles({
    table: {
      minWidth: 900,
      maxWidth: 1200,
    },
    backdropWrapper: {
      display: "flex",
      alignContent: "center",
      alignItems: "center",
      // zIndex: 199,
      width: "100%",
      height: "100%",
    },
  });
  const styles = useStyles();
  const gStyles = useGStyles();

  const [order, setOrder] = React.useState<Order>("asc");
  const [orderBy, setOrderBy] = React.useState<keyof presentationRow>("id");
  const [selected, setSelected] = React.useState<string[]>([]);
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);
  const [presentationToEdit, setPresentationToEdit] = useState<Presentation>();
  const [rows, setRows] = useState<Array<presentationRow>>([]);
  const presStore = PresentationContainer.useContainer();
  const lectStore = LecturerContainer.useContainer();

  const handleRequestSort = (
    event: React.MouseEvent<unknown>,
    property: keyof presentationRow
  ) => {
    const isAsc = orderBy === property && order === "asc";
    setOrder(isAsc ? "desc" : "asc");
    setOrderBy(property);
  };

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };
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
      const rows: Array<presentationRow> = presStore.presentations.map((p) => {
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
      });
      setRows(rows);
    };
    loadRows();
  }, [presStore.presentations]);

  const emptyRows =
    rowsPerPage -
    Math.min(rowsPerPage, presStore.presentations.length - page * rowsPerPage);

  return (
    <>
      <div className={gStyles.columnFlexDiv}>
        {presentationToEdit === undefined && (
          <>
            <TableContainer component={Paper} className={styles.table}>
              <Table aria-label="simple table" size={"small"}>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell align="right">Nummer</TableCell>
                    <TableCell align="right">Titel</TableCell>
                    <TableCell align="right">Schüler 1</TableCell>
                    <TableCell align="right">Schüler 2</TableCell>
                    <TableCell align="right">Coach</TableCell>
                    <TableCell align="right">Expert</TableCell>
                    <TableCell align="right">Typ</TableCell>
                    <TableCell align="right">Bearbeiten</TableCell>
                    <TableCell align="right">Löschen</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {rows!.length > 0 ? (
                    stableSort(rows!, getComparator(order, orderBy))
                      .slice(
                        page * rowsPerPage,
                        page * rowsPerPage + rowsPerPage
                      )
                      .map((p: presentationRow, index: number) => {
                        const labelId = `enhanced-table-checkbox-${index}`;
                        return (
                          <TableRow key={p.id}>
                            <TableCell component="th" scope="row">
                              {p.id}
                            </TableCell>
                            <TableCell>{p.nr}</TableCell>
                            <TableCell>{p.title}</TableCell>
                            <TableCell>{p.studentOne}</TableCell>
                            <TableCell>{p.studentTwo}</TableCell>
                            <TableCell>{p.coach}</TableCell>
                            <TableCell>{p.expert}</TableCell>
                            <TableCell>{p.type}</TableCell>
                            <TableCell>
                              <Button
                                className={gStyles.primaryButton}
                                onClick={() =>
                                  setPresentationToEdit(
                                    presStore.presentations.find(
                                      (pres: Presentation) => pres.id === p.id
                                    )
                                  )
                                }
                              >
                                <EditIcon />
                              </Button>
                            </TableCell>
                            <TableCell>
                              <Button
                                className={gStyles.secondaryButton}
                                onClick={() =>
                                  presStore.deletePresentationById(p.id)
                                }
                              >
                                <DeleteIcon />
                              </Button>
                            </TableCell>
                          </TableRow>
                        );
                      })
                  ) : (
                    <TableRow>
                      <TableCell>
                        <Typography variant="body1">
                          Derzeit keine Präsentation vorhanden
                        </Typography>
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
            <TablePagination
              rowsPerPageOptions={[5, 10, 25]}
              component="div"
              count={presStore.presentations.length}
              rowsPerPage={rowsPerPage}
              page={page}
              onChangePage={handleChangePage}
              onChangeRowsPerPage={handleChangeRowsPerPage}
            />
            <Button
              className={gStyles.primaryButton}
              onClick={() =>
                setPresentationToEdit({
                  title: "",
                  type: "normal",
                  nr: "",
                  studentOne: undefined,
                  studentTwo: undefined,
                })
              }
            >
              Präsentation Hinzufügen
            </Button>
          </>
        )}

        {/* {presentationToEdit !== undefined && (
        <div className={styles.backdropWrapper}> */}
        {/* <Backdrop
          // className={gStyles.myBackdrop}
          open={presentationToEdit !== undefined}
          //onClick={() => setPresentationToEdit(undefined)}
        > */}
        {presentationToEdit !== undefined && (
          <Paper className={gStyles.myPaper}>
            <PresentationEditForm
              onExitForm={() => setPresentationToEdit(undefined)}
              presentationId={presentationToEdit?.id}
              editPresentation={presentationToEdit?.id !== undefined}
            ></PresentationEditForm>
          </Paper>
        )}
      </div>
    </>
  );
};

export default PresentationTable;
