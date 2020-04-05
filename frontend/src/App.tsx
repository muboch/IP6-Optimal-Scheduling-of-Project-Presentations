import React from "react";
import { Redirect, Route, Switch } from "wouter";
import LandingScreen from "./screens/LandingScreen/landingscreen";
import { ThemeProvider } from "@material-ui/core";
import { theme, useGStyles } from "./theme";
import PlanningScreen from "./screens/CreatePlanningScreen/planningscreen";
import ListPlanningScreen from "./screens/ListPlanningScreen/listplanningscreen";
import { SCREENROUTES } from "./constants";
import UploadSucessfulScreen from "./screens/UploadSucessful/UploadSucessfulScreen";

const App: React.FC = (): JSX.Element => {
  console.log("endpoint", process.env.REACT_APP_API_ENDPOINT);
  console.log("nodeenv", process.env.NODE_ENV);

  const styles = useGStyles();
  return (
    <div className={styles.root}>
      <ThemeProvider theme={theme}>
        <Switch>
          <Route path={SCREENROUTES.landingScreen} component={LandingScreen} />
          <Route
            path={SCREENROUTES.createPlanning}
            component={PlanningScreen}
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
            path="/:rest*"
            component={() => (
              <Redirect to={SCREENROUTES.landingScreen}></Redirect>
            )}
          ></Route>
        </Switch>
      </ThemeProvider>
    </div>
  );
};

export default App;
