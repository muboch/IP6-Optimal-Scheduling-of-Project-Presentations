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
import EditScreen, { EditScreenProps } from "./screens/Edit Screens/editscreen";
import CreatePlanning from "./screens/CreatePlanning/createPlanning";
import MessageContainer from "./states/messageState";
import Notifier from "./Components/notifier";

const App: React.FC = (): JSX.Element => {
  console.log("endpoint", process.env.REACT_APP_API_ENDPOINT);
  console.log("nodeenv", process.env.NODE_ENV);

  const styles = useGStyles();

  return (
    <div className={styles.root}>
      <MessageContainer.Provider>
        <ThemeProvider theme={theme}>
          <Sidebar />
          <Switch>
            <Route
              path={SCREENROUTES.landingScreen}
              component={LandingScreen}
            />
            <Route path={SCREENROUTES.presentations}>
              {(params) => <EditScreen type={"presentation"}></EditScreen>}
            </Route>
            <Route path={SCREENROUTES.rooms}>
              {(params) => <EditScreen type={"room"}></EditScreen>}
            </Route>
            <Route path={SCREENROUTES.timeslots}>
              {(params) => <EditScreen type={"timeslot"}></EditScreen>}
            </Route>
            <Route path={SCREENROUTES.lecturers}>
              {(params) => <EditScreen type={"lecturer"}></EditScreen>}
            </Route>
            <Route path={SCREENROUTES.offtimes}>
              {(params) => <EditScreen type={"offtime"}></EditScreen>}
            </Route>

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
          <Notifier />
        </ThemeProvider>
      </MessageContainer.Provider>
    </div>
  );
};

export default App;
