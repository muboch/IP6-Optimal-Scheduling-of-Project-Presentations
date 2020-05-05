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
import React, { useState } from "react";
import { makeStyles } from "@material-ui/styles";
import { useGStyles } from "../../../theme";
import { stableSort, getComparator, Order } from "../../../Helpers/helpers";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";

import LecturerEditForm from "./lecturerEditForm";
import { deleteLecturerById } from "../../../Services/lecturerService";

export interface LecturerTableProps {
  lecturers: Array<Lecturer>;
}

const LecturerTable: React.SFC<LecturerTableProps> = ({ lecturers }) => {
  const useStyles = makeStyles({
    table: {
      minWidth: 900,
      maxWidth: 1200,
    },
  });
  const styles = useStyles();
  const gStyles = useGStyles();

  const [order, setOrder] = React.useState<Order>("asc");
  const [orderBy, setOrderBy] = React.useState<keyof lecturerRow>("id");
  const [selected, setSelected] = React.useState<string[]>([]);
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);

  const [lecturerToEdit, setLecturerToEdit] = useState<number>();

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
    rowsPerPage - Math.min(rowsPerPage, lecturers.length - page * rowsPerPage);
  const rows: Array<lecturerRow> = lecturers.map((l) => {
    return {
      id: l.id!,
      lastName: l.lastname,
      firstName: l.firstname,
      email: l.email,
      initials: l.initials,
    };
  });

  return (
    <>
      <div className={gStyles.columnFlexDiv}>
        <TableContainer component={Paper} className={styles.table}>
          <Table aria-label="simple table" size={"small"}>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell align="right">ID</TableCell>
                <TableCell align="right">Nachname</TableCell>
                <TableCell align="right">Vorname</TableCell>
                <TableCell align="right">Email</TableCell>
                <TableCell align="right">Kürzel</TableCell>
                <TableCell align="right">Bearbeiten</TableCell>
                <TableCell align="right">Löschen</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.length > 0 ? (
                stableSort(rows, getComparator(order, orderBy))
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((l: lecturerRow, index: number) => {
                    const labelId = `enhanced-table-checkbox-${index}`;
                    return (
                      <TableRow key={l.id}>
                        <TableCell component="th" scope="row">
                          {l.id}
                        </TableCell>
                        <TableCell align="right">{l.lastName}</TableCell>
                        <TableCell align="right">{l.firstName}</TableCell>
                        <TableCell align="right">{l.email}</TableCell>
                        <TableCell align="right">{l.initials}</TableCell>
                        <TableCell align="right">
                          <Button
                            className={gStyles.primaryButton}
                            onClick={() => setLecturerToEdit(l.id)}
                          >
                            <EditIcon />
                          </Button>
                        </TableCell>
                        <TableCell align="right">
                          <Button
                            className={gStyles.secondaryButton}
                            onClick={() => deleteLecturerById(l.id)}
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
          count={lecturers.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onChangePage={handleChangePage}
          onChangeRowsPerPage={handleChangeRowsPerPage}
        />
        <div>
          <Button
            className={gStyles.primaryButton}
            onClick={() => setLecturerToEdit(lecturers.length)}
          >
            Dozent Hinzufügen
          </Button>
        </div>
      </div>
      <Backdrop
        className={gStyles.backdrop}
        open={lecturerToEdit !== undefined}
        //onClick={() => setPresentationToEdit(undefined)}
      >
        <Paper className={gStyles.paper}>
          <LecturerEditForm
            onExitForm={() => setLecturerToEdit(undefined)}
            lecturerId={lecturerToEdit}
            editLecturer={lecturerToEdit! < lecturers.length}
          ></LecturerEditForm>
        </Paper>
      </Backdrop>
    </>
  );
};

export default LecturerTable;
