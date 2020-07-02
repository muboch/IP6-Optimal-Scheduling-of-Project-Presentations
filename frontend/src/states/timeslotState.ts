/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState, useEffect } from "react";
import { Timeslot } from "../Types/types";
import MessageContainer from "./messageState";
import {
  loadTimeslots,
  _addTimeslot,
  _deleteTimeslotById,
  _loadTimeslotById,
} from "../Services/timeslotService";

const timeslotState = () => {
  const msgStore = MessageContainer.useContainer();
  const [timeslots, setTimeslots] = useState<Array<Timeslot>>([]);

  useEffect(() => {
    const load = async () => {
      const l = await loadTimeslots();
      setTimeslots(l);
    };
    load();
  }, []);

  const invalidate = async (): Promise<void> => {
    try {
      await setTimeslots(await loadTimeslots());
    } catch (error) {
      msgStore.setMessage(error);
    }
  };

  const add = async (timeslot: Timeslot) => {
    try {
      await _addTimeslot(timeslot);
      msgStore.setMessage(`Zeitslot hinzugefügt / angepasst`);
      await invalidate();
    } catch (error) {
      msgStore.setMessage(`Fehler beim hinzufügen / anpassen: ${error}`);
      throw error;
    }
  };
  const deleteById = async (id: number) => {
    try {
      await _deleteTimeslotById(id);
      msgStore.setMessage(`Zeitslot mit id ${id} gelöscht`);
      await invalidate();
    } catch (error) {
      msgStore.setMessage(`Fehler beim löschen: ${error}`);
      throw error;
    }
  };
  const loadById = async (id: number) => {
    try {
      const timeslot = await _loadTimeslotById(id);
      return timeslot;
    } catch (error) {
      msgStore.setMessage(`Konnte Zeitslot mit id ${id} nicht laden: ${error}`);
      throw error;
    }
  };

  return {
    timeslots,
    invalidate,
    add,
    deleteById,
    loadById,
  };
};
const TimeslotContainer = createContainer(timeslotState);
export default TimeslotContainer;
