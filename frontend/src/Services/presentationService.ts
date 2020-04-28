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

export const loadPresentationById = async (id: number): Promise<Presentation> => {
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
}
