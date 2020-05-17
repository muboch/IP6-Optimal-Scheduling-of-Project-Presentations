import { Lecturer } from "../../../Types/types";
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

import LecturerEditForm from "./lecturerEditForm";
import { _deleteLecturerById } from "../../../Services/lecturerService";
import MessageContainer from "../../../states/messageState";
import LecturerContainer from "../../../states/lecturerState";

export interface LecturerTableProps {}

const LecturerTable: React.SFC<LecturerTableProps> = () => {
  const useStyles = makeStyles({
    table: {
      minWidth: 900,
      maxWidth: 1200,
    },
  });
  const styles = useStyles();
  const gStyles = useGStyles();
  const lecStore = LecturerContainer.useContainer();
  const msgStore = MessageContainer.useContainer();

  const [order, setOrder] = React.useState<Order>("asc");
  const [orderBy, setOrderBy] = React.useState<keyof lecturerRow>("id");
  const [selected, setSelected] = React.useState<string[]>([]);
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);
  const [rows, setRows] = useState<Array<lecturerRow>>([]);
  const [lecturerToEdit, setLecturerToEdit] = useState<Lecturer>();

  const handleRequestSort = (
    event: React.MouseEvent<unknown>,
    property: keyof lecturerRow
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
  type lecturerRow = {
    id: number;
    lastName: string;
    firstName: string;
    email: string;
    initials: string;
  };

  const emptyRows =
    rowsPerPage -
    Math.min(rowsPerPage, lecStore.lecturers.length - page * rowsPerPage);

  useEffect(() => {
    const loadRows = async () => {
      const rows: Array<lecturerRow> = lecStore.lecturers.map((l) => {
        return {
          id: l.id!,
          lastName: l.lastname,
          firstName: l.firstname,
          email: l.email,
          initials: l.initials,
        };
      });
      setRows(rows);
    };
    loadRows();
  }, [lecStore.lecturers]);

  return (
    <>
      <div className={gStyles.columnFlexDiv}>
        {lecturerToEdit == undefined && (
          <>
            <TableContainer component={Paper} className={styles.table}>
              <Table aria-label="simple table" size={"small"}>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Nachname</TableCell>
                    <TableCell>Vorname</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Kürzel</TableCell>
                    <TableCell>Bearbeiten</TableCell>
                    <TableCell>Löschen</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {rows!.length > 0 ? (
                    stableSort(rows, getComparator(order, orderBy))
                      .slice(
                        page * rowsPerPage,
                        page * rowsPerPage + rowsPerPage
                      )
                      .map((l: lecturerRow, index: number) => {
                        const labelId = `enhanced-table-checkbox-${index}`;
                        return (
                          <TableRow key={l.id}>
                            <TableCell component="th" scope="row">
                              {l.id}
                            </TableCell>
                            <TableCell>{l.lastName}</TableCell>
                            <TableCell>{l.firstName}</TableCell>
                            <TableCell>{l.email}</TableCell>
                            <TableCell>{l.initials}</TableCell>
                            <TableCell>
                              <Button
                                className={gStyles.primaryButton}
                                onClick={() =>
                                  setLecturerToEdit(
                                    lecStore.lecturers.find(
                                      (le) => le.id === l.id
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
                                onClick={() => {
                                  lecStore.deleteLecturerById(l.id);
                                }}
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
                          Derzeit keine Dozenten erfasst
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
              count={lecStore.lecturers.length}
              rowsPerPage={rowsPerPage}
              page={page}
              onChangePage={handleChangePage}
              onChangeRowsPerPage={handleChangeRowsPerPage}
            />
            <div>
              <Button
                className={gStyles.primaryButton}
                onClick={() =>
                  setLecturerToEdit({
                    email: "",
                    firstname: "",
                    initials: "",
                    lastname: "",
                  })
                }
              >
                Dozent Hinzufügen
              </Button>
            </div>
          </>
        )}

        {lecturerToEdit !== undefined && (
          <Paper className={gStyles.myPaper}>
            <LecturerEditForm
              onExitForm={() => setLecturerToEdit(undefined)}
              lecturerId={lecturerToEdit?.id}
              editLecturer={lecturerToEdit?.id !== undefined}
            ></LecturerEditForm>
          </Paper>
        )}
      </div>
    </>
  );
};

export default LecturerTable;
