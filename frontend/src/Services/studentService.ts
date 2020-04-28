import { APIROUTES } from "../constants";
import { Student } from "../Types/types";

export const loadStudents = async (): Promise<Array<Student>> => {
  try {
    const res = await fetch(`${APIROUTES.student}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }
    throw "Fehler beim laden der Schüler";
  } catch (Error) {
    throw Error;
  }
};
