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
    throw new Error("Fehler beim laden der Schüler");
  } catch (Error) {
    throw Error;
  }
};


export const _loadStudentById = async (id: number): Promise<Student> => {
  try {
    const res = await fetch(`${APIROUTES.student}/${id}`);
    const json = await res.json();
    console.log(json);
    if (res.ok) {
      return json;
    }

    throw new Error("Fehler beim laden des Schülers");
  } catch (Error) {
    throw Error;
  }
};

// ADD/UPDATE Presentation. If number is passed, then update existing, otherwise add new presentation
export const _addStudent = async (stud: Student): Promise<void> => {
  // Default options are marked with *
  const url = `${APIROUTES.student}`;
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
    body: JSON.stringify(stud), // body data type must match "Content-Type" header
  });
  return response.json(); // parses JSON response into native JavaScript objects
};

export const _deleteStudentById = async (id: number): Promise<void> => {
  // Default options are marked with *
  try {
    const res = await fetch(`${APIROUTES.student}/${id}`, {
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
