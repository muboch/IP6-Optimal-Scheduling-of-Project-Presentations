/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState, useEffect } from "react";
import { Room } from "../Types/types";

import MessageContainer from "./messageState";
import {
  loadRooms,
  _addRoom,
  _deleteRoomById,
  _loadRoomById,
} from "../Services/roomService";

const roomState = () => {
  const msgStore = MessageContainer.useContainer();
  const [rooms, setRooms] = useState<Array<Room>>([]);

  useEffect(() => {
    const load = async () => {
      const l = await loadRooms();
      setRooms(l);
    };
    load();
  }, []);

  const invalidate = async (): Promise<void> => {
    try {
      await setRooms(await loadRooms());
    } catch (error) {
      msgStore.setMessage(error);
    }
  };

  const add = async (room: Room) => {
    try {
      await _addRoom(room);
      msgStore.setMessage(`Zimmer hinzugefügt / angepasst`);
      await invalidate();
    } catch (error) {
      msgStore.setMessage(`Fehler beim hinzufügen / anpassen: ${error}`);
      throw error;
    }
  };
  const deleteById = async (id: number) => {
    try {
      await _deleteRoomById(id);
      msgStore.setMessage(`Zimmer mit id ${id} gelöscht`);
      await invalidate();
    } catch (error) {
      msgStore.setMessage(`Fehler beim löschen: ${error}`);
      throw error;
    }
  };
  const loadById = async (id: number) => {
    try {
      const room = await _loadRoomById(id);
      return room;
    } catch (error) {
      msgStore.setMessage(`Konnte Zimmer mit id ${id} nicht laden: ${error}`);
      throw error;
    }
  };

  return {
    rooms,
    invalidate,
    add,
    deleteById,
    loadById,
  };
};
const RoomContainer = createContainer(roomState);
export default RoomContainer;
