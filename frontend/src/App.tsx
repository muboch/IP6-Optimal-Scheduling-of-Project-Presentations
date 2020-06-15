import React from "react";
import { Redirect, Route, Switch } from "wouter";
import LandingScreen from "./screens/LandingScreen/landingscreen";
import { ThemeProvider } from "@material-ui/core";
import { theme, useGStyles } from "./theme";
import UploadFilesScreen from "./screens/UploadFilesScreen/upload-files-screen";
import ListPlanningScreen from "./screens/ListPlanningScreen/listplanningscreen";
import { SCREENROUTES } from "./constants";

import UploadSucessfulScreen from "./screens/UploadSucessful/UploadSucessfulScreen";
import Sidebar from "./Components/sidebar";
import CreatePlanning from "./screens/CreatePlanning/createPlanning";
import MessageContainer from "./states/messageState";
import Notifier from "./Components/notifier";
import LecturerContainer from "./states/lecturerState";
import PresentationContainer from "./states/presentationState";
import PresentationTable from "./screens/Edit Screens/presentation/presentationTable";
import LecturerTable from "./screens/Edit Screens/lecturer/lecturerTable";
import PresentationEditForm from "./screens/Edit Screens/presentation/presentationEditForm";
import LecturerEditForm from "./screens/Edit Screens/lecturer/lecturerEditForm";
import RoomTable from "./screens/Edit Screens/room/roomTable";
import RoomEditForm from "./screens/Edit Screens/room/roomEditForm";
import RoomContainer from "./states/roomState";
import StudentTable from "./screens/Edit Screens/student/studentTable";
import StudentEditForm from "./screens/Edit Screens/student/studentEditForm";
import StudentContainer from "./states/studentState";
import TimeslotTable from "./screens/Edit Screens/timeslot/timeslotTable";
import TimeslotEditForm from "./screens/Edit Screens/timeslot/timeslotEditForm";
import TimeslotContainer from "./states/timeslotState";
import OfftimeTable from "./screens/Edit Screens/offtime/offtimeTable";
import PlanningContainer from "./states/planningState";

const App: React.FC = (): JSX.Element => {
  console.log("endpoint", process.env.REACT_APP_API_ENDPOINT);
  console.log("nodeenv", process.env.NODE_ENV);

  const styles = useGStyles();

  return (
    <div className={styles.root}>
      <MessageContainer.Provider>
        <LecturerContainer.Provider>
          <PresentationContainer.Provider>
            <RoomContainer.Provider>
              <StudentContainer.Provider>
                <TimeslotContainer.Provider>
                  <PlanningContainer.Provider>
                    <ThemeProvider theme={theme}>
                      <Sidebar />
                      <div style={{ width: "100%", height: "100%" }}>
                        <Switch>
                          <Route
                            path={SCREENROUTES.landingScreen}
                            component={LandingScreen}
                          />
                          {/* Presentations */}
                          <Route path={SCREENROUTES.presentations}>
                            {(params) => <PresentationTable />}
                          </Route>
                          <Route
                            path={`${SCREENROUTES.presentations}/edit/:id`}
                          >
                            {(params) => (
                              <PresentationEditForm id={parseInt(params.id)} />
                            )}
                          </Route>
                          <Route path={`${SCREENROUTES.presentations}/edit`}>
                            {(params) => (
                              <PresentationEditForm id={undefined} />
                            )}
                          </Route>
                          {/* End Presentations */}
                          {/* Lecturers */}
                          <Route path={SCREENROUTES.lecturers}>
                            {(params) => <LecturerTable />}
                          </Route>
                          <Route path={`${SCREENROUTES.lecturers}/edit/:id`}>
                            {(params) => (
                              <LecturerEditForm id={parseInt(params.id)} />
                            )}
                          </Route>
                          <Route path={`${SCREENROUTES.lecturers}/edit`}>
                            {(params) => <LecturerEditForm id={undefined} />}
                          </Route>
                          {/* Rooms */}
                          <Route path={SCREENROUTES.rooms}>
                            {(params) => <RoomTable />}
                          </Route>
                          <Route path={`${SCREENROUTES.rooms}/edit/:id`}>
                            {(params) => (
                              <RoomEditForm id={parseInt(params.id)} />
                            )}
                          </Route>
                          <Route path={`${SCREENROUTES.rooms}/edit`}>
                            {(params) => <RoomEditForm id={undefined} />}
                          </Route>
                          {/* Students */}
                          <Route path={SCREENROUTES.students}>
                            {(params) => <StudentTable />}
                          </Route>
                          <Route path={`${SCREENROUTES.students}/edit/:id`}>
                            {(params) => (
                              <StudentEditForm id={parseInt(params.id)} />
                            )}
                          </Route>
                          <Route path={`${SCREENROUTES.students}/edit`}>
                            {(params) => <StudentEditForm id={undefined} />}
                          </Route>
                          {/* Timeslots */}
                          <Route path={SCREENROUTES.timeslots}>
                            {(params) => <TimeslotTable />}
                          </Route>
                          <Route path={`${SCREENROUTES.timeslots}/edit/:id`}>
                            {(params) => (
                              <TimeslotEditForm id={parseInt(params.id)} />
                            )}
                          </Route>
                          <Route path={`${SCREENROUTES.timeslots}/edit`}>
                            {(params) => <TimeslotEditForm id={undefined} />}
                          </Route>
                          {/* Offtimes */}
                          <Route path={SCREENROUTES.offtimes}>
                            {(params) => <OfftimeTable />}
                          </Route>

                          {/* <Route path={SCREENROUTES.rooms}>
                    {(params) => <EditScreen type={"room"}></EditScreen>}
                  </Route>
                  <Route path={SCREENROUTES.timeslots}>
                    {(params) => <EditScreen type={"timeslot"}></EditScreen>}
                  </Route>
                  <Route path={SCREENROUTES.offtimes}>
                    {(params) => <EditScreen type={"offtime"}></EditScreen>}
                  </Route> */}

                          <Route
                            path={SCREENROUTES.uploadFiles}
                            component={UploadFilesScreen}
                          />
                          <Route
                            path={SCREENROUTES.listPlanning}
                            component={ListPlanningScreen}
                          />
                          <Route
                            path={SCREENROUTES.uploadSucessful}
                            component={UploadSucessfulScreen}
                          />
                          <Route
                            path={SCREENROUTES.createPlanning}
                            component={CreatePlanning}
                          />
                          <Route
                            path="/:rest*"
                            component={() => (
                              <Redirect
                                to={SCREENROUTES.landingScreen}
                              ></Redirect>
                            )}
                          ></Route>
                        </Switch>
                      </div>
                      <Notifier />
                    </ThemeProvider>
                  </PlanningContainer.Provider>
                </TimeslotContainer.Provider>
              </StudentContainer.Provider>
            </RoomContainer.Provider>
          </PresentationContainer.Provider>
        </LecturerContainer.Provider>
      </MessageContainer.Provider>
    </div>
  );
};

export default App;
