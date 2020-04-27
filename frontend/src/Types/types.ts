export type Presentations = {
  id: number; // id used in Database, 1 based
  type: "normal" | "art" | "dance" | "music";
  externalId: number; // id used in solver, 0 based
  nr: "string";
  title: "string";
  coach: Lecturer;
  expert: Lecturer;
  studentOne: string;
  studentTwo: string;
  room?: Room;
  timeslot: string;
};

export type Room = {
  name: string;
  id: number; // id used in Database, 1 based
  type: "normal" | "art" | "dance" | "music";
  externalId: number; // id used in solver, 0 based
  place: string;
  reserve: boolean;
};

export type Lecturer = {
  id: number; // id used in Database, 1 based
  externalId: number; // id used in solver, 0 based
  firstname: string;
  lastname: string;
  email: string;
  initials: string;
};
