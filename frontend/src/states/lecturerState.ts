/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState, useEffect } from "react";
import { Lecturer } from "../Types/types";
import {
  loadLecturers,
  _addLecturer,
  _deleteLecturerById,
  _loadLecturerById,
} from "../Services/lecturerService";
import MessageContainer from "./messageState";

const lecturerState = () => {
  const msgStore = MessageContainer.useContainer();
  const [lecturers, setLecturers] = useState<Array<Lecturer>>([]);

  useEffect(() => {
    const load = async () => {
      const l = await loadLecturers();
      setLecturers(l);
    };
    load();
  }, []);

  const invalidate = async (): Promise<void> => {
    try {
      await setLecturers(await loadLecturers());
    } catch (error) {
      msgStore.setMessage(error);
    }
  };

  const add = async (lect: Lecturer) => {
    try {
      await _addLecturer(lect);
      msgStore.setMessage(`Lehrperson hinzugefügt / angepasst`);
    } catch (error) {
      msgStore.setMessage(`Fehler beim hinzufügen / anpassen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const deleteById = async (id: number) => {
    try {
      await _deleteLecturerById(id);
      msgStore.setMessage(`Lehrperson mit id ${id} gelöscht`);
    } catch (error) {
      msgStore.setMessage(`Fehler beim löschen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const loadById = async (id: number) => {
    try {
      const lect = await _loadLecturerById(id);
      return lect;
    } catch (error) {
      msgStore.setMessage(`Konnte Lehrperson mit id ${id} nicht laden: ${error}`);
    }
  };

  return {
    lecturers,
    invalidateLecturers: invalidate,
    addLecturer: add,
    deleteLecturerById: deleteById,
    loadLecturerById: loadById,
  };
};
const LecturerContainer = createContainer(lecturerState);
export default LecturerContainer;
