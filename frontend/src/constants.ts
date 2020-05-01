export const SCREENROUTES = {
  uploadSucessful: "/uploadSucessful",
  createPlanning: "/createPlanning",
  listPlanning: "/listPlanning",
  landingScreen: "/",
  presentations: "/presentations",
  rooms: "/rooms",
  lecturers: "/lecturers",
  offtimes: "/offtimes",
  timeslots: "/timeslots",
};

export const APIROUTES = {
  presentation: `${process.env.REACT_APP_API_ENDPOINT}/api/presentation`,
  lecturer: `${process.env.REACT_APP_API_ENDPOINT}/api/lecturer`,
  student: `${process.env.REACT_APP_API_ENDPOINT}/api/student`,
};

export const PRESENTATIONTYPES = ["normal", "art", "music", "dance"];
