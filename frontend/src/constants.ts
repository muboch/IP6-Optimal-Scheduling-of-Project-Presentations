export const SCREENROUTES = {
  uploadSucessful: "/uploadSucessful",
  uploadFiles: "/upload",
  listPlanning: "/listPlanning",
  createPlanning: "/createPlanning",
  landingScreen: "/",
  presentations: "/presentations",
  rooms: "/rooms",
  lecturers: "/lecturers",
  offtimes: "/offtimes",
  timeslots: "/timeslots",
  students: "/students",
};

export const APIROUTES = {
  presentation: `${process.env.REACT_APP_API_ENDPOINT}/api/presentation`,
  lecturer: `${process.env.REACT_APP_API_ENDPOINT}/api/lecturer`,
  student: `${process.env.REACT_APP_API_ENDPOINT}/api/student`,
  planning: `${process.env.REACT_APP_API_ENDPOINT}/api/planning`,
  room: `${process.env.REACT_APP_API_ENDPOINT}/api/room`,
  timeslot: `${process.env.REACT_APP_API_ENDPOINT}/api/timeslot`,
};

export const PRESENTATIONTYPES = ["normal", "art", "music", "dance"];
