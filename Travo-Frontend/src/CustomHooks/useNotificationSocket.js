/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { useEffect, useRef } from "react";
import { Client } from '@stomp/stompjs';
import SockJS from "sockjs-client";
import { useDispatch, useSelector } from "react-redux";
import { addNewNotification } from "../Redux/Slices/notificationSlice";
import { apiRequest } from "../lib/api";

const useNotificationSocket = () => {
  const loggedInUser = useSelector((state) => state.auth.user);
  const notifications = useSelector((state) => state.notifications?.notifications || []);
  const dispatch = useDispatch();
  const knownNotificationIds = useRef(new Set());

  const dispatchIfNew = (notification) => {
    const notificationId = notification?.notificationId;
    if (!notificationId || knownNotificationIds.current.has(notificationId)) return;

    knownNotificationIds.current.add(notificationId);
    dispatch(addNewNotification(notification));
  };

  useEffect(() => {
    notifications.forEach((notification) => {
      if (notification?.notificationId) {
        knownNotificationIds.current.add(notification.notificationId);
      }
    });
  }, [notifications]);

  useEffect(() => {
    if (!loggedInUser?.userId) return;

    const token = localStorage.getItem("token");
    if (!token) return;

    knownNotificationIds.current = new Set(
      notifications.map((notification) => notification?.notificationId).filter(Boolean),
    );

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
            dispatchIfNew(JSON.parse(message.body));
          } catch (error) {
            console.error("Invalid notification payload:", error);
          }
        });
      },
      onDisconnect: () => {
        console.warn("Notification WebSocket disconnected; unread polling remains active.");
      },
      onStompError: (frame) => {
        console.error("WebSocket error:", frame.headers["message"]);
      },
      onWebSocketError: (error) => {
        console.error("❌ WebSocket error:", error);
      },
    });

    stompClient.activate();

    const pollUnreadNotifications = async () => {
      try {
        const { data } = await apiRequest(
          "http://localhost:8085/api/v1/notification?read=false",
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        );

        (Array.isArray(data) ? data : []).forEach(dispatchIfNew);
      } catch (error) {
        console.warn("Unread notification fallback failed:", error?.message);
      }
    };

    const pollingId = window.setInterval(pollUnreadNotifications, 10000);

    return () => {
      window.clearInterval(pollingId);
      stompClient.deactivate();
    };
  }, [loggedInUser?.userId, dispatch]);
};

export default useNotificationSocket;
