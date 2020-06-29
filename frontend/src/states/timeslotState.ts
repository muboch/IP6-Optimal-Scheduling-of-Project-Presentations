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
    } catch (error) {
      msgStore.setMessage(`Fehler beim hinzufügen / anpassen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const deleteById = async (id: number) => {
    try {
      await _deleteTimeslotById(id);
      msgStore.setMessage(`Zeitslot mit id ${id} gelöscht`);
    } catch (error) {
      msgStore.setMessage(`Fehler beim löschen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const loadById = async (id: number) => {
    try {
      const timeslot = await _loadTimeslotById(id);
      return timeslot;
    } catch (error) {
      msgStore.setMessage(`Konnte Zeitslot mit id ${id} nicht laden: ${error}`);
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
