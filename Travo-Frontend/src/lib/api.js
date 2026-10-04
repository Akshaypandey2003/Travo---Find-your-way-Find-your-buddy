const STATUS_MESSAGES = {
  400: "The request could not be processed.",
  401: "Please log in to continue.",
  403: "You do not have permission to perform this action.",
  404: "The requested resource was not found.",
  409: "This request conflicts with existing data.",
  422: "Please check the submitted values.",
  500: "The server could not complete the request.",
};

export const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8085"
).replace(/\/+$/, "");

const resolveApiUrl = (url) => {
  const localApiOrigin = "http://localhost:8085";
  return typeof url === "string" && url.startsWith(localApiOrigin)
    ? `${API_BASE_URL}${url.slice(localApiOrigin.length)}`
    : url;
};

const isObject = (value) => value !== null && typeof value === "object";

const extractMessage = (body) => {
  if (typeof body === "string" && body.trim()) return body.trim();
  if (!isObject(body)) return "";

  const message =
    body.message ||
    body.messageResponse?.message ||
    body.messageReponse?.message ||
    body.error?.message;

  if (message) return String(message);

  if (isObject(body.errors)) {
    return Object.values(body.errors).filter(Boolean).join(" ");
  }

  return "";
};

const parseResponseBody = async (response) => {
  if (response.status === 204) return null;

  const text = await response.text();
  if (!text.trim()) return null;

  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
};

export class ApiError extends Error {
  constructor(message, { status, data, response } = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.data = data;
    this.response = response;
  }
}

export const apiRequest = async (url, options = {}) => {
  let response;

  try {
    response = await fetch(resolveApiUrl(url), options);
  } catch (error) {
    throw new ApiError(
      "Unable to reach the server. Please check your connection and try again.",
      { cause: error },
    );
  }

  const body = await parseResponseBody(response);
  const message = extractMessage(body);
  const data = isObject(body) && Object.prototype.hasOwnProperty.call(body, "data")
    ? body.data
    : body;
  const bodyStatus = isObject(body) && typeof body.status === "string"
    ? body.status.toLowerCase()
    : "";
  const success = response.ok && body?.success !== false && !["error", "failure"].includes(bodyStatus);
  const result = {
    success,
    message: message || (success ? "" : STATUS_MESSAGES[response.status] || "Request failed."),
    data,
    status: response.status,
    raw: body,
  };

  if (!success) {
    throw new ApiError(
      result.message || STATUS_MESSAGES[response.status] || "Request failed.",
      { status: response.status, data: body, response },
    );
  }

  return result;
};

export const getApiErrorMessage = (error) =>
  error?.message || "Unable to complete the request. Please try again.";
