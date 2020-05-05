import { createMuiTheme, makeStyles } from "@material-ui/core/styles";

export const theme = createMuiTheme({
  palette: {
    primary: {
      // light: will be calculated from palette.primary.main,
      main: "#225A33",
      // dark: will be calculated from palette.primary.main,
      contrastText: "#FFFFFF",
      // contrastText: will be calculated to contrast with palette.primary.main
    },
    secondary: {
      light: "#0066ff",
      main: "#0044ff",
      // dark: will be calculated from palette.secondary.main,
      contrastText: "#ffcc00",
    },
    // Used by `getContrastText()` to maximize the contrast between
    // the background and the text.
    contrastThreshold: 3,
    // Used by the functions below to shift a color's luminance by approximately
    // two indexes within its tonal palette.
    // E.g., shift from Red 500 to Red 300 or Red 700.
    tonalOffset: 0.2,
  },
});

export const useGStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
    height: "100vh",
    display: "flex",
    alignContent: "center",
    justifyContent: "center",
    alignItems: "center",
  },
  centerFlexDiv: {
    display: "flex",
    alignContent: "center",
    justifyContent: "center",
    alignItems: "center",
    width: "100%",
    height: "100%",
    "& > *": {
      marginLeft: theme.spacing(1),
      marginRight: theme.spacing(1),
    },
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
      marginLeft: theme.spacing(1),
      marginRight: theme.spacing(1),
    },
  },
  primaryButton: {
    color: theme.palette.primary.contrastText,
    backgroundColor: theme.palette.primary.main,
    padding: "4px",
    "&.Mui-disabled": {
      background: "#AAB9AE",
    },
    margin: "8px",
  },

  secondaryButton: {
    color: theme.palette.primary.main,
    backgroundColor: theme.palette.primary.contrastText,
    borderWidth: "1px",
    borderColor: theme.palette.primary.main,
    padding: "4px",
    margin: "8px",
  },

  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
    padding: 0,
    margin: 0,
  },
  paper: {
    minWidth: 650,
    maxWidth: 1000,
    padding: "20px",
  },
}));
