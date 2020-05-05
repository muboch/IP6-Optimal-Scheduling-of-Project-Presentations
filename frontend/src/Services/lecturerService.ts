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

export const loadLecturerById = async (id: number): Promise<Lecturer> => {
  try {
    const res = await fetch(`${APIROUTES.lecturer}/${id}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }
    throw "Fehler beim laden des Dozenten";
  } catch (Error) {
    throw Error;
  }
};

// ADD/UPDATE Presentation. If number is passed, then update existing, otherwise add new presentation
export const addLecturer = async (
  lect: Lecturer,
  id?: number
): Promise<void> => {
  // Default options are marked with *
  const url = id ? `${APIROUTES.lecturer}/${id}` : `${APIROUTES.lecturer}`;
  const response = await fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    mode: "cors", // no-cors, *cors, same-origin
    cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    credentials: "same-origin", // include, *same-origin, omit
    headers: {
      "Content-Type": "application/json",
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    redirect: "follow", // manual, *follow, error
    referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    body: JSON.stringify({ lecturer: lect }), // body data type must match "Content-Type" header
  });
  return response.json(); // parses JSON response into native JavaScript objects
};

export const deleteLecturerById = async (id: number): Promise<void> => {
  // Default options are marked with *
  const response = await fetch(`${APIROUTES.lecturer}/${id}`, {
    method: "DELETE", // *GET, POST, PUT, DELETE, etc.
    mode: "cors", // no-cors, *cors, same-origin
    cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    credentials: "same-origin", // include, *same-origin, omit
    headers: {
      "Content-Type": "application/json",
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    redirect: "follow", // manual, *follow, error
    referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
  });
  return response.json(); // parses JSON response into native JavaScript objects
};
