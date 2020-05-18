/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState, useEffect } from "react";
import { Student } from "../Types/types";
import MessageContainer from "./messageState";
import {
  loadStudents,
  _addStudent,
  _deleteStudentById,
  _loadStudentById,
} from "../Services/studentService";

const studentState = () => {
  const msgStore = MessageContainer.useContainer();
  const [students, setStudents] = useState<Array<Student>>([]);

  useEffect(() => {
    const load = async () => {
      const l = await loadStudents();
      setStudents(l);
    };
    load();
  }, []);

  const invalidate = async (): Promise<void> => {
    try {
      await setStudents(await loadStudents());
    } catch (error) {
      msgStore.setMessage(error);
    }
  };

  const add = async (student: Student) => {
    try {
      await _addStudent(student);
      msgStore.setMessage(`Schüler hinzugefügt / angepasst`);
    } catch (error) {
      msgStore.setMessage(`Fehler beim hinzufügen / anpassen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const deleteById = async (id: number) => {
    try {
      await _deleteStudentById(id);
      msgStore.setMessage(`Schüler mit id ${id} gelöscht`);
    } catch (error) {
      msgStore.setMessage(`Fehler beim löschen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const loadById = async (id: number) => {
    try {
      const student = await _loadStudentById(id);
      return student;
    } catch (error) {
      msgStore.setMessage(`Konnte Schüler mit id ${id} nicht laden: ${error}`);
    }
  };

  return {
    students,
    invalidate,
    add,
    deleteById,
    loadById,
  };
};
const StudentContainer = createContainer(studentState);
export default StudentContainer;
