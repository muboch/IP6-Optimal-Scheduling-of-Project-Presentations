import React, { useState, useEffect } from "react";
import {
  Button,
  useEventCallback,
  TableContainer,
  Paper,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  makeStyles,
} from "@material-ui/core";
import { useGStyles } from "../../theme";
import { useLocation } from "wouter";
import { SCREENROUTES, APIROUTES } from "../../constants";
import { loadConsistency } from "../../Services/planningService";
import { ConsistencyError } from "../../Types/types";
import WarningIcon from "@material-ui/icons/Warning";
import ErrorIcon from "@material-ui/icons/Error";
import { yellow } from "@material-ui/core/colors";

const CreatePlanning: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const [location, setLocation] = useLocation();
  const [consistencyChecks, setConsistencyChecks] = useState<Array<any>>([]);

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

        <Button
          className={gStyles.primaryButton}
          target="_blank"
          href={`${APIROUTES.planning}/example`}
        >
          Planung erstellen
        </Button>
      </div>
    </div>
  );
};

export default CreatePlanning;
