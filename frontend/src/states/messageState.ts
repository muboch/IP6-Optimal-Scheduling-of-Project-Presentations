/* eslint-disable react-hooks/rules-of-hooks */
import { createContainer } from "unstated-next";
import { useState } from "react";

const messageState = () => {
  const [message, setMessage] = useState<string>("");
  return { message, setMessage };
};
const MessageContainer = createContainer(messageState);
export default MessageContainer;
