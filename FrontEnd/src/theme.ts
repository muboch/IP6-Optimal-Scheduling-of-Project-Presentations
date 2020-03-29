import {createMuiTheme, makeStyles} from "@material-ui/core/styles";

export const theme = createMuiTheme({
  palette: {
    primary: {
      // light: will be calculated from palette.primary.main,
      main: "#225A33",
      // dark: will be calculated from palette.primary.main,
      contrastText: "#FFFFFF"
      // contrastText: will be calculated to contrast with palette.primary.main
    },
    secondary: {
      light: "#0066ff",
      main: "#0044ff",
      // dark: will be calculated from palette.secondary.main,
      contrastText: "#ffcc00"
    },
    // Used by `getContrastText()` to maximize the contrast between
    // the background and the text.
    contrastThreshold: 3,
    // Used by the functions below to shift a color's luminance by approximately
    // two indexes within its tonal palette.
    // E.g., shift from Red 500 to Red 300 or Red 700.
    tonalOffset: 0.2
  }
});

export const useGStyles = makeStyles(theme => ({
  root: { width: "100vw", height: "100vh" },
  centerFlexDiv: {
    display: "flex",
    alignContent: "center",
    justifyContent: "center",
    alignItems: "center",
    width: "100%",
    height: "100%",
    "& > *": {
      margin: theme.spacing(1)
    }
  },
  columnFlexDiv: {
    display: "flex",
    flexDirection: "column",
    alignContent: "center",
    justifyContent: "center",
    alignItems: "center",
    width: "100%",
    height: "100%",
    "& > *": {
      margin: theme.spacing(1)
    }
  },
  primaryButton: { color: theme.palette.primary.contrastText, backgroundColor: theme.palette.primary.main, padding: "4px" },
  secondaryButton: {
    color: theme.palette.primary.main,
    backgroundColor: theme.palette.primary.contrastText,
    borderWidth: "1px",
    borderColor: theme.palette.primary.main,
    padding: "4px"
  }
}));
