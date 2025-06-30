/* eslint-disable no-unused-vars */
import { useEffect } from "react";
import { useDispatch } from "react-redux";

const useWebSocket = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    const ws = new WebSocket("ws://localhost:8085/ws");

    ws.onopen = () => {
      console.log("WebSocket connected");
    };

    ws.onmessage = (event) => {
      console.log("WebSocket notification:", event.data);

      // Optionally parse and dispatch
      // const notification = JSON.parse(event.data);
    //   dispatch();
    };

    ws.onerror = (err) => {
      console.error("WebSocket error:", err);
    };

    ws.onclose = () => {
      console.log("WebSocket disconnected");
    };

    return () => {
      ws.close(); // Cleanup on component unmount
    };
  }, [dispatch]);
};

export default useWebSocket;
