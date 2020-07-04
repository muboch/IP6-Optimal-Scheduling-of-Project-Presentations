import React, { useEffect } from "react";
import { Button } from "@material-ui/core";
import { useGStyles } from "../../theme";
import { useLocation } from "wouter";
import { SCREENROUTES } from "../../constants";
import { useContainer } from "unstated-next";
import LecturerContainer from "../../states/lecturerState";
import PresentationContainer from "../../states/presentationState";
import RoomContainer from "../../states/roomState";
import StudentContainer from "../../states/studentState";

const UploadSucessfulScreen: React.FC = (): JSX.Element => {
  const gStyles = useGStyles();
  const [, setLocation] = useLocation();
  const lecturerState = useContainer(LecturerContainer);
  const presentationState = useContainer(PresentationContainer);
  const roomState= useContainer(RoomContainer);
  const studentState = useContainer(StudentContainer);

  useEffect(() => {
   lecturerState.invalidateLecturers();
   presentationState.invalidatePresentations();
   roomState.invalidate();
   studentState.invalidate(); 
  },[])

  return (
    <div className={gStyles.centerFlexDiv}>
      <p>
          Die Dateien wurden hochgeladen. Die Entitäten können nun bearbeitet werden.
      </p>
      <Button
        className={gStyles.primaryButton}
        onClick={() => {
          setLocation(SCREENROUTES.createPlanning);
        }}
      >
        Eine Planung erstellen
      </Button>
    </div>
  );
};

export default UploadSucessfulScreen;
