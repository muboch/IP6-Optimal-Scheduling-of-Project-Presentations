import React, { useEffect, useState } from "react";
import {
  Button,
  Paper,
  TableContainer,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  makeStyles,
  Typography,
} from "@material-ui/core";
import { useGStyles } from "../../theme";
import DeleteIcon from "@material-ui/icons/Delete";
import { APIROUTES } from "../../constants";
import SolvingStatus from "../../Components/solvingStatus";
import { deletePlanningById } from "../../Services/planningService";

type Plannings = {
  nr: string;
  name: string;
  id: string;
  created: string;
  status?: string;
};

const useStyles = makeStyles({
  table: {
    minWidth: 650,
    maxWidth: 1000,
  },
  spinner: { maxWidth: 20, maxHeight: 20 },
});

const ListPlanningScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const styles = useStyles();
  const [plannings, setPlannings] = useState<Array<Plannings>>([]);
  const [spinner, setSpinner] = useState<Boolean>(false);

  const loadData = async () => {
    setSpinner(true);
    try {
      const res = await fetch(APIROUTES.planning);
      const json = await res.json();
      console.log(json);
      setPlannings(json);
    } catch (Error) {
    } finally {
      setTimeout(() => {
        setSpinner(false);
      }, 500);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <div className={gStyles.columnFlexDiv}>
      <TableContainer component={Paper} className={styles.table}>
        <Table aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Nummer</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Erstellungsdatum</TableCell>
              <TableCell>Download</TableCell>
              <TableCell>Löschen</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {plannings.length > 0 ? (
              plannings.map((p) => (
                <TableRow key={p.id}>
                  <TableCell component="th" scope="row">
                    {p.name}
                  </TableCell>
                  <TableCell>{p.nr}</TableCell>
                  <TableCell>
                    {p.status === "SOLUTION"
                      ? "Lösung gefunden"
                      : "Keine Lösung gefunden"}
                  </TableCell>
                  <TableCell>{p.created}</TableCell>
                  <TableCell>
                    <Button
                      className={gStyles.primaryButton}
                      target="_blank"
                      href={`${APIROUTES.planning}/${p.id}`}
                    >
                      Planung Herunterladen
                    </Button>
                  </TableCell>
                  <TableCell>
                    <Button
                      className={gStyles.primaryButton}
                      onClick={() => {
                        deletePlanningById(parseInt(p.id));
                        setTimeout(() => {
                          loadData();
                        }, 1000);
                      }}
                    >
                      <DeleteIcon></DeleteIcon>
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell>
                  <Typography variant="body1">
                    Derzeit keine Planung vorhanden
                  </Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
      <Button
        type="submit"
        className={`${gStyles.primaryButton}`}
        onClick={() => {
          loadData();
        }}
      >
        Neu laden
        {spinner && (
          <div className={styles.spinner}>{/* <CircularProgress /> */}</div>
        )}
      </Button>
      <SolvingStatus />
    </div>
  );
};

export default ListPlanningScreen;
