import React, {useEffect, useState} from "react";
import {Button, FormControl, InputLabel, Link, MenuItem, Select} from "@material-ui/core";
import {useGStyles} from "../../theme";
import {API} from "../../constants";

type Plannings = {
  id: string;
  name: string;
  fileUrl: string;
};

const ListPlanningScreen: React.FC = (): JSX.Element => {
  const styles = useGStyles();
  const [plannings, setPlannings] = useState<Array<Plannings>>();
  const [selectedPlanning, setSelectedPlanning] = useState<string>();

  useEffect(() => {
    const loadData = async () => {
      const res = await fetch(`${API.endpoint}/plannings`);
      const json = await res.json();
      setPlannings(JSON.parse(json));
    };
    loadData();
  }, []);

  return (
    <div className={styles.columnFlexDiv}>
      <FormControl>
        <InputLabel id="select-planing-label">Planung</InputLabel>
        <Select
          labelId="select-planing-label"
          onChange={e => {
            setSelectedPlanning(e.target.value as string);
          }}
        >
          <MenuItem value={undefined}>
            <em>Planung auswählen</em>
          </MenuItem>
          {plannings?.map(p => {
            return <MenuItem value={p.id}>{p.name}</MenuItem>;
          })}
        </Select>
      </FormControl>
      <Link href={plannings?.find(p => p.id === selectedPlanning)?.fileUrl}>
        <Button
          disabled={selectedPlanning === undefined}
          className={styles.primaryButton}
        >
          Planung Herunterladen
        </Button>
      </Link>
    </div>
  );
};

export default ListPlanningScreen;
