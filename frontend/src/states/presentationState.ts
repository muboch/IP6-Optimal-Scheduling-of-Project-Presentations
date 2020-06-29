/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState, useEffect } from "react";
import { Presentation } from "../Types/types";

import MessageContainer from "./messageState";
import {
  _loadPresentations,
  _loadPresentationById,
  _deletePresentationById,
  _addPresentation,
} from "../Services/presentationService";

const presentationState = () => {
  const msgStore = MessageContainer.useContainer();
  const [presentations, setPresentations] = useState<Array<Presentation>>([]);

  useEffect(() => {
    const load = async () => {
      const l = await _loadPresentations();
      setPresentations(l);
    };
    load();
  }, []);

  const invalidate = async (): Promise<void> => {
    try {
      setPresentations(await _loadPresentations());
    } catch (error) {
      msgStore.setMessage(error);
    }
  };

  const add = async (pres: Presentation) => {
    try {
      await _addPresentation(pres);
      msgStore.setMessage(`Präsentation hinzugefügt / angepasst`);
    } catch (error) {
      msgStore.setMessage(`Fehler beim hinzufügen / anpassen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const deleteById = async (id: number) => {
    try {
      await _deletePresentationById(id);
      msgStore.setMessage(`Präsentation mit id ${id} gelöscht`);
    } catch (error) {
      msgStore.setMessage(`Fehler beim löschen: ${error}`);
    } finally {
      await invalidate();
    }
  };
  const loadById = async (id: number) => {
    try {
      const lect = await _loadPresentationById(id);
      return lect;
    } catch (error) {
      msgStore.setMessage(
        `Konnte Präsentation mit id ${id} nicht laden: ${error}`
      );
    }
  };

  return {
    presentations,
    invalidatePresentations: invalidate,
    addPresentation: add,
    deletePresentationById: deleteById,
    loadPresentationById: loadById,
  };
};
const PresentationContainer = createContainer(presentationState);
export default PresentationContainer;
