import React from "react";
import { Switch, Route, Redirect } from "wouter";
import LandingScreen from "./screens/LandingScreen/landingscreen";
import { ThemeProvider } from "@material-ui/core";
import { theme, useGStyles } from "./theme";
import { StylesContext } from "@material-ui/styles";
import PlanningScreen from "./screens/CreatePlanningScreen/planningscreen";
import ListPlanningScreen from "./screens/ListPlanningScreen/listplanningscreen";

const App: React.FC = (): JSX.Element => {
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
