import React, { useState, useEffect } from "react";
import {
  Button,
  TableContainer,
  Paper,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  makeStyles,
  Snackbar,
} from "@material-ui/core";
import { useGStyles } from "../../theme";
import { loadConsistency, firePlanning } from "../../Services/planningService";
import { ConsistencyError } from "../../Types/types";
import WarningIcon from "@material-ui/icons/Warning";
import ErrorIcon from "@material-ui/icons/Error";
import SolvingStatus from "../../Components/solvingStatus";
import SolvingProgress from "../../Components/solvingProgress";

const CreatePlanning: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const [bDisabled, setBDisabled] = useState(false);
  const [consistencyChecks, setConsistencyChecks] = useState<Array<any>>([]);
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [errorMsg, setErrorMsg] = useState<string>("");

  useEffect(() => {
    const load = async () => {
      const consistency = await loadConsistency();
      setConsistencyChecks(consistency);
    };

    load();
  }, []);

  const useStyles = makeStyles({
    table: {
      minWidth: 900,
      maxWidth: 1200,
      maxHeight: "80%",
    },
    tableBody: {
      maxHeight: "50%",
      overflow: "auto",
    },
  });
  const styles = useStyles();

  const yellowWarnIcon = <WarningIcon style={{ color: "#FFCC00" }} />;
  const redErrorIcon = <ErrorIcon style={{ color: "FF0000" }} />;

  return (
    <div className={gStyles.columnFlexDiv}>
      <div className={gStyles.columnFlexDiv}>
        <TableContainer component={Paper} className={styles.table}>
          <Table stickyHeader aria-label="simple table" size={"small"}>
            <TableHead>
              <TableRow>
                <TableCell align="center">Status</TableCell>
                <TableCell>Details</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {consistencyChecks &&
                consistencyChecks.map((c: ConsistencyError) => {
                  return (
                    <TableRow key={c.message}>
                      <TableCell component="th" scope="row" align="center">
                        {c.status === "WARN" && yellowWarnIcon}
                        {c.status === "ERROR" && redErrorIcon}
                      </TableCell>
                      <TableCell component="th" scope="row">
                        {c.message}
                      </TableCell>
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
        </TableContainer>
        <SolvingStatus />
        <SolvingProgress />

        <Button
          className={gStyles.primaryButton}
          disabled={bDisabled}
          onClick={() => {
            setBDisabled(true);
            try {
              firePlanning();
            } catch (error) {
              setSnackbarOpen(true);
              setErrorMsg(error);
            } finally {
              setBDisabled(false);
            }
          }}
        >
          Planung erstellen
        </Button>
        <Snackbar
          open={snackbarOpen}
          autoHideDuration={10000}
          onClose={() => {
            setSnackbarOpen(false);
            setErrorMsg("");
          }}
          message={`Fehler beim starten der Planung: ${errorMsg}`}
        />
      </div>
    </div>
  );
};

export default CreatePlanning;
