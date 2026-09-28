import {
  apiFailed,
  apiStarted,
  apiSucceeded,
} from "../Redux/Slices/apiStatusSlice";

const notify = (dispatch, { message, success, error, requestKey, statusCode }) => {
  if (!message) return;

  dispatch(success
    ? apiSucceeded({ message, requestKey, statusCode })
    : apiFailed({ message, error, requestKey, statusCode }));
};

export const startApiRequest = (dispatch, requestKey) =>
  dispatch(apiStarted(requestKey));

export const notifyApiSuccess = (dispatch, message, requestKey) =>
  notify(dispatch, { message, success: true, error: false, requestKey });

export const notifyApiError = (dispatch, message, requestKey) =>
  notify(dispatch, { message, success: false, error: true, requestKey });
