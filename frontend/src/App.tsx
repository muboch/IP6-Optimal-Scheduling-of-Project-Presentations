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
import { create } from "jss";
import { StylesProvider, jssPreset } from "@material-ui/core/styles";

const App: React.FC = (): JSX.Element => {
  console.log("endpoint", process.env.REACT_APP_API_ENDPOINT);
  console.log("nodeenv", process.env.NODE_ENV);

  const styles = useGStyles();

  return (
    <div className={styles.root}>
      <MessageContainer.Provider>
        <LecturerContainer.Provider>
          <PresentationContainer.Provider>
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
                  <Route path={`${SCREENROUTES.presentations}/edit/:id`}>
                    {(params) => (
                      <PresentationEditForm id={parseInt(params.id)} />
                    )}
                  </Route>
                  <Route path={`${SCREENROUTES.presentations}/edit`}>
                    {(params) => <PresentationEditForm id={undefined} />}
                  </Route>
                  {/* End Presentations */}

                  <Route path={SCREENROUTES.lecturers}>
                    {(params) => <LecturerTable />}
                  </Route>
                  <Route path={`${SCREENROUTES.lecturers}/edit/:id`}>
                    {(params) => <LecturerEditForm id={parseInt(params.id)} />}
                  </Route>
                  <Route path={`${SCREENROUTES.lecturers}/edit`}>
                    {(params) => <LecturerEditForm id={undefined} />}
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
                      <Redirect to={SCREENROUTES.landingScreen}></Redirect>
                    )}
                  ></Route>
                </Switch>
              </div>
              <Notifier />
            </ThemeProvider>
          </PresentationContainer.Provider>
        </LecturerContainer.Provider>
      </MessageContainer.Provider>
    </div>
  );
};

export default App;
