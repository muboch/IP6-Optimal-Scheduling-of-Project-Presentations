import { APIROUTES } from "../constants";
import { Lecturer, Room } from "../Types/types";

export const loadRooms = async (): Promise<Array<Room>> => {
  try {
    const res = await fetch(`${APIROUTES.room}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }
    throw "Fehler beim laden der Zimmer";
  } catch (Error) {
    throw Error;
  }
};

export const _loadRoomById = async (id: number): Promise<Room> => {
  try {
    const res = await fetch(`${APIROUTES.room}/${id}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }

    throw "Fehler beim laden des Zimmers";
  } catch (Error) {
    throw Error;
  }
};

// ADD/UPDATE Presentation. If number is passed, then update existing, otherwise add new presentation
export const _addRoom = async (lect: Room): Promise<void> => {
  // Default options are marked with *
  const url = `${APIROUTES.room}`;
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
    body: JSON.stringify(lect), // body data type must match "Content-Type" header
  });
  return response.json(); // parses JSON response into native JavaScript objects
};

export const _deleteRoomById = async (id: number): Promise<void> => {
  // Default options are marked with *
  try {
    const res = await fetch(`${APIROUTES.room}/${id}`, {
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
    if (res.ok) {
      return;
    }
    const json = await res.json();
    throw json.message;
  } catch (error) {
    throw error;
  }
};
