package kr.hhplus.be.server.interfaces.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import kr.hhplus.be.server.domain.user.UserPointService;
import kr.hhplus.be.server.interfaces.api.point.PointRequest;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPointRepository userPointRepository;

    @Test
    void 사용자_포인트_충전_성공() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 100L;
        UserPoint originUser = userPointRepository.findByUserId(userId);
        PointRequest request = new PointRequest(userId, amount);

        // when & then
        mockMvc.perform(post("/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.CHARGE_SUCCESS));

        UserPoint userPoint = userPointRepository.findByUserId(userId);
        Long expectedAmount = originUser.getPoint() + amount;
        assertNotNull(userPoint);
        assertEquals(userPoint.getPoint(), expectedAmount);
    }

    @Test
    void 사용자_포인트_충전_실패_400() throws Exception{
        // given
        Long userId = -100000L;
        Long point = 500L;
        PointRequest request = new PointRequest(userId, point);

        // when & then
        mockMvc.perform(post("/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ApiMessage.INVALID_USER));
    }

    @Test
    void 사용자_포인트_사용_성공() throws Exception {
        // given
        Long userId = 1L;
        Long point = 300L;
        PointRequest request = new PointRequest(userId, point);

        // when & then
        mockMvc.perform(post("/points/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.USE_SUCCESS));
    }

    @Test
    void 사용자_포인트_사용_실패_400() throws Exception {
        // given
        Long userId = -10000L;
        Long point = 300L;
        PointRequest request = new PointRequest(userId, point);

        // when & then
        mockMvc.perform(post("/points/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ApiMessage.INVALID_USER));
    }
}
