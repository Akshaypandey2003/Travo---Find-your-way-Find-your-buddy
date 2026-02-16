package com.user.UnitTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Config.JwtProvider;
import com.user.Controller.ConnectionController;
import com.user.Controller.UserController;
import com.user.DTO.ConnectionResponse;
import com.user.Entity.Connections;
import com.user.ServiceImpl.ConnectionServiceImpl;

@WebMvcTest(ConnectionController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal","unused"})
public class ConnectionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionServiceImpl connectionService;

    @Autowired
    private ObjectMapper objectMapper;

     @MockBean
    private JwtProvider jwtProvider; 

     @Test
    void testSendRequest() throws Exception {

        Connections connections = new Connections();
        connections.setFollowerId("1");
        connections.setFollowingId("2");

        Mockito.when(connectionService.sendFollowRequest("1", "2"))
                .thenReturn(new ConnectionResponse());

        mockMvc.perform(post("/connection/send/1/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ---------------- acceptRequest ----------------

    @Test
    void testAcceptRequest() throws Exception {

        Mockito.doNothing().when(connectionService)
                .acceptFollowRequest("1", "2");

        mockMvc.perform(post("/connection/accept/100/1/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend Request Accepted Successfully!!"));
    }

    // ---------------- getReceivedRequests ----------------

//     @Test
//     void testGetReceivedRequests() throws Exception {

//         Mockito.when(connectionService.getReceivedRequests("1"))
//                 .thenReturn(List.of(new Connections()));

//         mockMvc.perform(get("/connection/received/1"))
//                 .andExpect(status().isOk());
//     }

    // ---------------- deleteConnection ----------------

    @Test
    void testDeleteConnection() throws Exception {

        Mockito.doNothing().when(connectionService)
                .rejectFollowRequest("1","10");

        mockMvc.perform(delete("/connection/delete/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend Request Rejected Successfully!!"));
    }

    // ---------------- getSentRequests ----------------

//     @Test
//     void testGetSentRequests() throws Exception {

//         Mockito.when(connectionService.getSentRequests("1"))
//                 .thenReturn(List.of(new Connections()));

//         mockMvc.perform(get("/connection/sent/1"))
//                 .andExpect(status().isOk());
//     }

    // ---------------- getAllConnectionRequests ----------------

//     @Test
//     void testGetAllConnectionRequests() throws Exception {

//         Mockito.when(connectionService.getAll())
//                 .thenReturn(List.of(new Connections()));

//         mockMvc.perform(get("/connection/get-all"))
//                 .andExpect(status().isOk());
//     }
}
