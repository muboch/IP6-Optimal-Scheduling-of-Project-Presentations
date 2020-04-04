import React, { useEffect, useState } from "react";
import {
  Button,
  FormControl,
  InputLabel,
  Link,
  MenuItem,
  Select,
  Paper,
  TableContainer,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  makeStyles
} from "@material-ui/core";
import { useGStyles } from "../../theme";


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
  }
});

const ListPlanningScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const styles = useStyles();
  const [plannings, setPlannings] = useState<Array<Plannings>>();

  useEffect(() => {
    const loadData = async () => {
      const res = await fetch(`${process.env.API_ENDPOINT}/api/plannings`);
      const json = await res.json();
      console.log(json);

      setPlannings(json);
    };
    loadData();
  }, []);

  const downloadFile = async (id: string) => {
    const res = await fetch(`${process.env.API_ENDPOINT}/api/plannings/${id}`);
  };

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
            {plannings &&
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
                      href={`${process.env.API_ENDPOINT}/api/plannings/${p.id}`}
                    >
                      Planung Herunterladen
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default ListPlanningScreen;
