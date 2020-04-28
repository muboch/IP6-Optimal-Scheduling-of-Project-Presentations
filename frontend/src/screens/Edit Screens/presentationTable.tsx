import { Presentation } from "../../Types/types";
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
} from "@material-ui/core";
import React, { useState } from "react";
import { makeStyles } from "@material-ui/styles";
import { useGStyles } from "../../theme";
import { stableSort, getComparator, Order } from "../../Helpers/helpers";
import EditIcon from "@material-ui/icons/Edit";

export interface PresentationTableProps {
  presentations: Array<Presentation>;
  setPresentationToEdit: (pres: number) => void;
}

const PresentationTable: React.SFC<PresentationTableProps> = ({
  presentations,
  setPresentationToEdit
}) => {
  const useStyles = makeStyles({
    table: {
      minWidth: 900,
      maxWidth: 1200,
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

  const emptyRows =
    rowsPerPage -
    Math.min(rowsPerPage, presentations.length - page * rowsPerPage);
  const rows: Array<presentationRow> = presentations.map((p) => {
    return {
      id: p.externalId,
      title: p.title,
      studentOne: p.studentOne.name,
      studentTwo: p.studentTwo?.name,
      coach: `${p.coach.lastname}, ${p.coach.firstname}`,
      expert: `${p.expert.lastname}, ${p.expert.firstname}`,
      type: p.type as string,
      nr: p.nr,
    };
  });

  return (
    <div className={gStyles.columnFlexDiv}>
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
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.length > 0 ? (
              stableSort(rows, getComparator(order, orderBy))
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((p: presentationRow, index: number) => {
                  const labelId = `enhanced-table-checkbox-${index}`;
                  return (
                    <TableRow key={p.id}>
                      <TableCell component="th" scope="row">
                        {p.id}
                      </TableCell>
                      <TableCell align="right">{p.nr}</TableCell>
                      <TableCell align="right">{p.title}</TableCell>
                      <TableCell align="right">{p.studentOne}</TableCell>
                      <TableCell align="right">{p.studentTwo}</TableCell>
                      <TableCell align="right">{p.coach}</TableCell>
                      <TableCell align="right">{p.expert}</TableCell>
                      <TableCell align="right">{p.type}</TableCell>
                      <TableCell align="right">
                        <Button
                          className={gStyles.primaryButton}
                          onClick={() => setPresentationToEdit(p.id)}
                        >
                          <EditIcon />
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
        count={presentations.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onChangePage={handleChangePage}
        onChangeRowsPerPage={handleChangeRowsPerPage}
      />
    </div>
  );
};

export default PresentationTable;
