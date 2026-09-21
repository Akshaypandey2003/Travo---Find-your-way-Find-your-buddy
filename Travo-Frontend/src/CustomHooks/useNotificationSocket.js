/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { useEffect } from "react";
import { Client } from '@stomp/stompjs';
import SockJS from "sockjs-client";
import { useDispatch, useSelector } from "react-redux";
import { addNewNotification } from "../Redux/Slices/notificationSlice";

const useNotificationSocket = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();

  useEffect(() => {
    if (!loggedInUser?.userId) return;

    const token = localStorage.getItem("token");
    if (!token) return;

    const stompClient = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8085/ws"),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("Connected to WebSocket");

        stompClient.subscribe("/user/queue/notifications", (message) => {
          try {
            dispatch(addNewNotification(JSON.parse(message.body)));
          } catch (error) {
            console.error("Invalid notification payload:", error);
          }
        });
      },
      onStompError: (frame) => {
        console.error("WebSocket error:", frame.headers["message"]);
      },
      onWebSocketError: (error) => {
        console.error("❌ WebSocket error:", error);
      },
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [loggedInUser, dispatch]);
};

export default useNotificationSocket;
