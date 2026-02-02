/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { useEffect } from "react";
// import SockJS from 'sockjs-client/dist/sockjs';
import { Client } from '@stomp/stompjs';
import { useDispatch, useSelector } from "react-redux";
import { addNewNotification, addNotification } from "../Redux/Slices/notificationSlice";
import { addChats, addMessageToChat } from "../Redux/Slices/chatSlice";
import useTrip from "./useTrip";

const useNotificationSocket = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();
  const {getTrip} = useTrip();

  
  useEffect(() => {
    if (!loggedInUser?.userId) return;
    
    console.log("This is notification socket in frontend");
    // const socket = new SockJS('http://localhost:8088/ws'); // or gateway/ws

    
    const stompClient = new Client({
      brokerURL: "ws://localhost:8080/ws",
      // webSocketFactory: () => socket,
      onConnect: () => {
        console.log("Connected to WebSocket");

        stompClient.subscribe(`/topic/notifications/${loggedInUser.userId}`, (message) => {
          const notification = JSON.parse(message.body);
          console.log('Notification received:', notification);
          
          if(!notification?.messageId && !notification?.chatId)
            dispatch(addNewNotification(notification))

          notification?.messageId ? dispatch(addMessageToChat({chatId:notification?.chatId,message:notification})): dispatch(addChats(notification));
          
          // e.g., dispatch notification to Redux or show toast
        });
      },
      onStompError: (frame) => {
        console.error("WebSocket error:", frame.headers["message"]);
      },
      onWebSocketError: (error) => {
        console.error("❌ WebSocket error:", error);
      },
      debug: (str) => {
        console.log("📡 STOMP Debug:", str);
      }
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [loggedInUser]);
};

export default useNotificationSocket;
