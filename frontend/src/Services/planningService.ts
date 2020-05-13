import { APIROUTES } from "../constants";
import { Lecturer, ConsistencyError } from "../Types/types";

export const loadPlannings = async (): Promise<Array<Lecturer>> => {
  try {
    const res = await fetch(`${APIROUTES.planning}`);
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

export const loadConsistency = async (): Promise<Array<ConsistencyError>> => {
  try {
    const res = await fetch(`${APIROUTES.planning}/consistency`);
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

export const getPlanningById = async (id: number): Promise<Lecturer> => {
  try {
    const res = await fetch(`${APIROUTES.planning}/${id}`);
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

export const deletePlanningById = async (id: number): Promise<void> => {
  // Default options are marked with *
  const response = await fetch(`${APIROUTES.planning}/${id}`, {
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
