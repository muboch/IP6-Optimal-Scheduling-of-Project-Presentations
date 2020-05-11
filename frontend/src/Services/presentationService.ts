import { APIROUTES } from "../constants";
import { Presentation } from "../Types/types";

export const loadPresentations = async (): Promise<Array<Presentation>> => {
  try {
    const res = await fetch(`${APIROUTES.presentation}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }
    throw "Fehler beim laden der Präsentationen";
  } catch (Error) {
    throw Error;
  }
};

export const loadPresentationById = async (
  id: number
): Promise<Presentation> => {
  try {
    const res = await fetch(`${APIROUTES.presentation}/${id}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }
    throw "Fehler beim laden der Präsentation";
  } catch (Error) {
    throw Error;
  }
};

// ADD/UPDATE Presentation. If number is passed, then update existing, otherwise add new presentation
export const addPresentation = async (
  pres: Presentation,
): Promise<void> => {
  // Default options are marked with *
  const response = await fetch(`${APIROUTES.presentation}`, {
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
    body: JSON.stringify(pres), // body data type must match "Content-Type" header
  });
  return response.json(); // parses JSON response into native JavaScript objectsu
};

export const deletePresentationById = async (id: number): Promise<void> => {
  try {
    const res = await fetch(`${APIROUTES.presentation}/${id}`, {
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
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }
    throw "Fehler beim löschen der Präsentation";
  } catch (Error) {
    throw Error;
  }
};