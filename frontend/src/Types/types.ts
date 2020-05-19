export type Presentation = {
  id?: number; // id used in Database, 1 based
  type: "normal" | "art" | "dance" | "music";
  nr: string;
  title: string;
  coach?: Lecturer;
  expert?: Lecturer;
  studentOne?: Student;
  studentTwo?: Student;
  room?: Room;
  timeslot?: string;
};

export type ConsistencyError = {
  status: "ERROR" | "WARN";
  message: string;
};

export type Room = {
  name: string;
  id?: number; // id used in Database, 1 based
  type: "normal" | "art" | "dance" | "music";
  place: string;
  reserve: boolean;
};

export type Offtime = {
  id: number;
  date: string;
  block: number;
};

export type Lecturer = {
  id?: number; // id used in Database, 1 based
  firstname: string;
  lastname: string;
  email: string;
  initials: string;
  offtimes: Array<Offtime>;
};

export type Student = {
  name: string;
  id?: number;
  class: string;
};

export type Timeslot = {
  id?: number;
  date: string;
  block: number;
  priority: number;
};
