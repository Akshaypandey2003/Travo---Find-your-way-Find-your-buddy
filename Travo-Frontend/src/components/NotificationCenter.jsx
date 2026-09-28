import { useMemo } from "react";
import { useSelector } from "react-redux";
import {
  Bell,
  Check,
  ChevronDown,
  Heart,
  MessageCircle,
  Plane,
  UserPlus,
  X,
} from "lucide-react";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import useNotificationsData from "../CustomHooks/useNotificationsData";
import useTrip from "../CustomHooks/useTrip";
import useUserData from "../CustomHooks/useUserData";

const DEFAULT_PROFILE_PIC = "https://ui-avatars.com/api/?name=Travo&background=f97316&color=fff";

const GROUPS = [
  { key: "requests", label: "Requests", types: ["FRIEND_REQUEST", "TRIP_REQUEST_SENT", "TRIP_REQUEST"] },
  { key: "activity", label: "Activity", types: ["BLOG_LIKED", "COMMENT_ADDED", "COMMENT_LIKED", "LIKE", "COMMENT", "NEW_FOLLOWER"] },
  { key: "trips", label: "Trips", types: ["TRIP_CREATED", "NEW_TRIP", "TRIP_REQUEST_ACCEPTED", "ACCEPTED", "TRIP_INVITE"] },
];

const notificationType = (notification) => notification?.type?.toUpperCase();

const getTripId = (notification) =>
  notification?.tripId || notification?.resourceId || notification?.metadata?.tripId;

const NotificationCard = ({ notification }) => {
  const { acceptFriendRequest } = useUserData();
  const { acceptTripRequest, sendTripRequest } = useTrip();
  const { deleteNotification } = useNotificationsData();
  const type = notificationType(notification);
  const isFriendRequest = type === "FRIEND_REQUEST";
  const isTripRequest = type === "TRIP_REQUEST_SENT" || type === "TRIP_REQUEST";
  const isNewTrip = type === "TRIP_CREATED" || type === "NEW_TRIP";
  const hasActions = isFriendRequest || isTripRequest || isNewTrip;
  const tripId = getTripId(notification);

  const accept = () => {
    if (isFriendRequest) {
      acceptFriendRequest(
        notification.notificationId,
        notification.notificationFrom,
        notification.notificationTo,
      );
    } else if (isTripRequest) {
      acceptTripRequest(notification.notificationId, tripId, notification.notificationFrom);
    } else {
      sendTripRequest({
        notificationId: notification.notificationId,
        tripId,
        requestTo: notification.notificationFrom,
      });
    }
  };

  return (
    <div className="flex items-start gap-3 rounded-xl border border-slate-200 bg-white p-3 shadow-sm dark:border-slate-700 dark:bg-surface-dark">
      <Avatar className="h-10 w-10 border border-primary/20">
        <AvatarImage src={notification?.senderProfilePic || DEFAULT_PROFILE_PIC} alt="" />
        <AvatarFallback>{notification?.senderName?.charAt(0) || "T"}</AvatarFallback>
      </Avatar>
      <div className="min-w-0 flex-1">
        <p className="text-sm leading-5 text-slate-700 dark:text-slate-200">
          {/* <span className="font-semibold text-slate-900 dark:text-white">{notification?. senderName|| "Travo"}</span>{" "} */}
          {notification?.message || "sent you a notification"}
        </p>
        {hasActions ? (
          <div className="mt-3 flex gap-2">
            <button onClick={accept} className="inline-flex items-center gap-1 rounded-lg bg-primary px-3 py-1.5 text-xs font-semibold text-white transition-colors hover:bg-primary-hover">
              <Check size={14} />
              {isNewTrip ? "Join" : "Accept"}
            </button>
            <button onClick={() => deleteNotification(notification.notificationId)} className="inline-flex items-center gap-1 rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-600 transition-colors hover:border-red-300 hover:bg-red-50 hover:text-red-600 dark:border-slate-600 dark:text-slate-300 dark:hover:bg-red-950/30">
              <X size={14} />
              Delete
            </button>
          </div>
        ) : (
          <button onClick={() => deleteNotification(notification.notificationId)} className="mt-2 inline-flex items-center gap-1 text-xs font-medium text-slate-400 hover:text-red-500">
            <X size={14} />
            Dismiss
          </button>
        )}
      </div>
    </div>
  );
};

const groupIcon = (key) => {
  if (key === "requests") return UserPlus;
  if (key === "activity") return Heart;
  return Plane;
};

const NotificationCenter = () => {
  const notifications = useSelector((store) => store.notifications?.notifications || []);
  const { getAllNotifications } = useNotificationsData();
  const groupedNotifications = useMemo(
    () => GROUPS.map((group) => ({ ...group, items: notifications.filter((item) => group.types.includes(notificationType(item))) })),
    [notifications],
  );
  const ungrouped = notifications.filter(
    (item) => !GROUPS.some((group) => group.types.includes(notificationType(item))),
  );
  const groups = ungrouped.length
    ? [...groupedNotifications, { key: "other", label: "Updates", types: [], items: ungrouped }]
    : groupedNotifications;
  const count = notifications.length;

  const handleOpenChange = (open) => {
    if (open) {
      getAllNotifications();
    }
  };

  return (
    <Dialog onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        <button aria-label="Open notifications" className="relative flex h-10 w-10 items-center justify-center rounded-full text-slate-500 transition-colors hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-surface-dark">
          <Bell size={20} />
          {/* {count > 0 && <span className="absolute -right-1 -top-1 min-w-5 rounded-full bg-primary px-1.5 py-0.5 text-[10px] font-bold leading-4 text-white">{count}</span>} */}
          {count > 0 && <span className="absolute right-2.5 top-2.5 h-2 w-2 rounded-full border-2 border-white bg-red-500 dark:border-background-dark" />}
        </button>
      </DialogTrigger>
      <DialogContent className="max-h-[85vh] overflow-hidden border-slate-200 bg-slate-50 p-0 dark:border-slate-700 dark:bg-background-dark sm:max-w-[550px]">
        <DialogHeader className="border-b border-slate-200 bg-white px-6 py-5 text-left dark:border-slate-700 dark:bg-surface-darker">
          <DialogTitle className="flex items-center gap-2 text-xl text-slate-900 dark:text-white"><Bell size={20} className="text-primary" /> Notifications</DialogTitle>
          <DialogDescription>See how people are interacting with your profile and get the latest updates.</DialogDescription>
        </DialogHeader>
        <ScrollArea className="h-[min(60vh,420px)] px-6 pb-6">
          {count === 0 ? (
            <div className="flex flex-col items-center gap-2 py-16 text-center">
              <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10 text-primary"><MessageCircle size={28} /></div>
              <h2 className="text-lg font-semibold text-slate-700 dark:text-slate-200">No new notifications</h2>
              <p className="text-sm text-slate-500">You&apos;re all caught up. Check back later.</p>
            </div>
          ) : (
            <Accordion type="multiple" defaultValue={groups.filter((group) => group.items.length).map((group) => group.key)} className="pt-2">
              {groups.filter((group) => group.items.length).map((group) => {
                const Icon = groupIcon(group.key);
                return (
                  <AccordionItem value={group.key} key={group.key} className="border-slate-200 dark:border-slate-700">
                    <AccordionTrigger className="text-sm font-semibold text-slate-700 hover:no-underline dark:text-slate-200"><span className="flex items-center gap-2"><Icon size={16} className="text-primary" /> {group.label}<span className="rounded-full bg-primary/10 px-2 py-0.5 text-xs text-primary">{group.items.length}</span></span></AccordionTrigger>
                    <AccordionContent className="space-y-2">
                      {group.items.map((notification) => <NotificationCard key={notification.notificationId} notification={notification} />)}
                    </AccordionContent>
                  </AccordionItem>
                );
              })}
            </Accordion>
          )}
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
};

export default NotificationCenter;