import { APIROUTES } from "../constants";
import { Lecturer } from "../Types/types";

export const loadLecturers = async (): Promise<Array<Lecturer>> => {
  try {
    const res = await fetch(`${APIROUTES.lecturer}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }
    throw "Fehler beim laden der Dozenten";
  } catch (Error) {
    throw Error;
  }
};
