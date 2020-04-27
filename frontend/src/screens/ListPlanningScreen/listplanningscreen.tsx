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
  CircularProgress
} from "@material-ui/core";
import { useGStyles } from "../../theme";
import { useLocation } from "wouter";

type Plannings = {
  nr: string;
  name: string;
  id: string;
  status?: string;
};

const useStyles = makeStyles({
  table: {
    minWidth: 650,
    maxWidth: 1000
  },
  spinner: { maxWidth: 20, maxHeight: 20 }
});

const ListPlanningScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const styles = useStyles();
  const [, setLocation] = useLocation();
  const [plannings, setPlannings] = useState<Array<Plannings>>([]);
  const [spinner, setSpinner] = useState<Boolean>(false);

  const loadData = async () => {
    setSpinner(true);
    try {
      const res = await fetch(
        `${process.env.REACT_APP_API_ENDPOINT}/api/plannings`
      );
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
              <TableCell align="right">Nummer</TableCell>
              <TableCell align="right">Status</TableCell>
              <TableCell align="right">Download</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {plannings.length > 0 ? (
              plannings.map(p => (
                <TableRow key={p.id}>
                  <TableCell component="th" scope="row">
                    {p.name}
                  </TableCell>
                  <TableCell align="right">{p.nr}</TableCell>
                  <TableCell align="right">{p.status}</TableCell>
                  <TableCell align="right">
                    <Button
                      className={gStyles.primaryButton}
                      target="_blank"
                      href={`${process.env.REACT_APP_API_ENDPOINT}/api/plannings/${p.id}`}
                    >
                      Planung Herunterladen
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
          <div className={styles.spinner}>
            {/* <CircularProgress /> */}
          </div>
        )}
      </Button>
    </div>
  );
};

export default ListPlanningScreen;
