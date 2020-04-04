import React from "react";
import { Redirect, Route, Switch } from "wouter";
import LandingScreen from "./screens/LandingScreen/landingscreen";
import { ThemeProvider } from "@material-ui/core";
import { theme, useGStyles } from "./theme";
import PlanningScreen from "./screens/CreatePlanningScreen/planningscreen";
import ListPlanningScreen from "./screens/ListPlanningScreen/listplanningscreen";

const App: React.FC = (): JSX.Element => {
  console.log("endpoint", process.env.API_ENDPOINT);

  const styles = useGStyles();
  return (
    <div className={styles.root}>
      <ThemeProvider theme={theme}>
        <Switch>
          <Route path="/" component={LandingScreen} />
          <Route path="/createPlanning" component={PlanningScreen} />
          <Route path="/listPlanning" component={ListPlanningScreen} />
          <Route
            path="/:rest*"
            component={() => <Redirect to="/"></Redirect>}
          ></Route>
        </Switch>
      </ThemeProvider>
    </div>
  );
};

export default App;
