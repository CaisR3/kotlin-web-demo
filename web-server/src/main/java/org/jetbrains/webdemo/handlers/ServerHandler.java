/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.webdemo.handlers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.webdemo.ErrorWriter;
import org.jetbrains.webdemo.ResponseUtils;
import org.jetbrains.webdemo.help.HelpLoader;
import org.jetbrains.webdemo.session.SessionInfo;
import org.jetbrains.webdemo.session.UserInfo;
import org.jetbrains.webdemo.sessions.MyHttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

public class ServerHandler {


    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        if (request.getQueryString() != null && request.getQueryString().equals("test")) {
            try (PrintWriter out = response.getWriter()) {
                out.write("ok");
            } catch (Throwable e) {
                ErrorWriter.ERROR_WRITER.writeExceptionToExceptionAnalyzer(e,
                        "TEST", request.getHeader("Origin"), "null");
            }
        } else {
            SessionInfo sessionInfo;
            try {
                switch (request.getParameter("type")) {
                    case ("getSessionInfo"):
                        sessionInfo = setSessionInfo(request.getSession(), request.getHeader("Origin"));
                        sendSessionInfo(request, response, sessionInfo);
                        break;
                    case ("getUserName"):
                        sessionInfo = setSessionInfo(request.getSession(), request.getHeader("Origin"));
                        sendUserName(request, response, sessionInfo);
                        break;
                    case ("sendMail"):
                        sessionInfo = setSessionInfo(request.getSession(), request.getHeader("Origin"));
                        sessionInfo.setType(SessionInfo.TypeOfRequest.SEND_MAIL);
                        sendMail(request, response, sessionInfo);
                        break;
                    case ("loadHelpForWords"):
                        sendHelpContentForWords(request, response);
                        break;
                    default: {
                        sessionInfo = setSessionInfo(request.getSession(), request.getHeader("Origin"));
                        MyHttpSession session = new MyHttpSession(sessionInfo);
                        session.handle(request, response);
                    }
                }
            } catch (Throwable e) {
                //Do not stop server
                ErrorWriter.ERROR_WRITER.writeExceptionToExceptionAnalyzer(e,
                        "UNKNOWN", "unknown", request.getRequestURI() + "?" + request.getQueryString());
                ResponseUtils.writeResponse(request, response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private void sendMail(HttpServletRequest request, HttpServletResponse response, SessionInfo sessionInfo) {
        try {
            String from = request.getParameter("email");
            String name = request.getParameter("name");
            String title = request.getParameter("subject");
            String message = request.getParameter("question");
//            MailAgent.getInstance().send(from, name, title, message);
        } catch (NullPointerException e) {
            writeResponse(request, response, "Can't get parameters", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void sendSessionInfo(HttpServletRequest request, HttpServletResponse response, SessionInfo sessionInfo) {
        try {
            String id = sessionInfo.getId();
            ObjectNode responseBody = new ObjectNode(JsonNodeFactory.instance);
            responseBody.put("id", id);
            responseBody.put("isLoggedIn", sessionInfo.getUserInfo().isLogin());
            writeResponse(request, response, responseBody.toString(), HttpServletResponse.SC_OK);
        } catch (Throwable e) {
            ErrorWriter.ERROR_WRITER.writeExceptionToExceptionAnalyzer(e,
                    "UNKNOWN", sessionInfo.getOriginUrl(), request.getRequestURI() + "?" + request.getQueryString());
        }
    }

    private void sendUserName(HttpServletRequest request, HttpServletResponse response, SessionInfo sessionInfo) {
        try {
            ObjectNode responseBody = new ObjectNode(JsonNodeFactory.instance);
            if (sessionInfo.getUserInfo().isLogin()) {
                responseBody.put("isLoggedIn", true);
                responseBody.put("userName", URLEncoder.encode(sessionInfo.getUserInfo().getName(), "UTF-8"));
                responseBody.put("type", sessionInfo.getUserInfo().getType());
            } else {
                responseBody.put("isLoggedIn", false);
            }
            writeResponse(request, response, responseBody.toString(), HttpServletResponse.SC_OK);
        } catch (Throwable e) {
            ErrorWriter.ERROR_WRITER.writeExceptionToExceptionAnalyzer(e,
                    "UNKNOWN", sessionInfo.getOriginUrl(), request.getRequestURI() + "?" + request.getQueryString());
        }
    }

    @NotNull
    private SessionInfo setSessionInfo(final HttpSession session, String originUrl) {
        SessionInfo sessionInfo = new SessionInfo(session.getId());
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        if (userInfo == null) {
            userInfo = new UserInfo();
            session.setAttribute("userInfo", userInfo);
        }
        sessionInfo.setUserInfo(userInfo);
        sessionInfo.setOriginUrl(originUrl);
        return sessionInfo;
    }

    private void sendHelpContentForWords(HttpServletRequest request, final HttpServletResponse response) {
        writeResponse(request, response, HelpLoader.getInstance().getHelpForWords(), 200);
    }


    //Send Response
    private void writeResponse(HttpServletRequest request, HttpServletResponse response, String responseBody, int errorCode) {
        try {
            ResponseUtils.writeResponse(request, response, responseBody, errorCode);
        } catch (IOException e) {
            //This is an exception we can't send data to client
            ErrorWriter.ERROR_WRITER.writeExceptionToExceptionAnalyzer(e, "UNKNOWN", request.getHeader("Origin"), "null");
        }
    }
}

