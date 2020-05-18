// import { Lecturer } from "../../../Types/types";
// import { Button } from "@material-ui/core";
// import React, { useState, useEffect } from "react";
// import { makeStyles } from "@material-ui/styles";
// import { useGStyles } from "../../../theme";
// import { Order } from "../../../Helpers/helpers";
// import MaterialTable from "material-table";

// import { _deleteLecturerById } from "../../../Services/lecturerService";
// import LecturerContainer from "../../../states/lecturerState";
// import { useLocation } from "wouter";
// import { SCREENROUTES } from "../../../constants";

// export interface LecturerTableProps {}

// const RoomTable: React.SFC<LecturerTableProps> = () => {
//   const gStyles = useGStyles();
//   const lecStore = LecturerContainer.useContainer();
//   const [, setRows] = useState<Array<lecturerRow>>([]);
//   const [lecturerToEdit] = useState<Lecturer>();
//   const [, setLocation] = useLocation();

//   useEffect(() => {
//     // const loadRows = async () => {
//     //   const rows: Array<lecturerRow> = lecStore.lecturers.map((l) => {
//     //     return {
//     //       id: l.id!,
//     //       lastName: l.lastname,
//     //       firstName: l.firstname,
//     //       email: l.email,
//     //       initials: l.initials,
//     //     };
//     //   });
//     //   setRows(rows);
//     // };
//     // loadRows();
//   }, [lecStore.lecturers]);

//   const columns = [
//     {
//       title: "Name",
//       field: "name",
//     },
//     {
//       title: "id",
//       field: "id",
//       type: "numeric",
//     },
//     {
//       title: "Typ",
//       field: "type",
//     },
//     {
//       title: "Ort",
//       field: "place",
//     },
//     {
//       title: "Reserve",
//       field: "reserve",
//     },
//   ];

//   return (
//     <>
//       <div className={gStyles.columnFlexDiv}>
//         {lecturerToEdit == undefined && (
//           <>
//             <MaterialTable
//               title="Zimmer"
//               columns={columns}
//               data={}
//             ></MaterialTable>

//             <div>
//               <Button
//                 className={gStyles.primaryButton}
//                 onClick={() => {
//                   setLocation(`${SCREENROUTES.lecturers}/edit`);
//                 }}
//               >
//                 Dozent Hinzufügen
//               </Button>
//             </div>
//           </>
//         )}
//       </div>
//     </>
//   );
// };

// export default RoomTable;
