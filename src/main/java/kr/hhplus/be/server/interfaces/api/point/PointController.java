package kr.hhplus.be.server.interfaces.api.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.user.UserPointService;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
@Tag(name = "📌 포인트 관리", description = "포인트 관련 API 모음")
public class PointController {

    private final UserPointService userPointService;

    @PostMapping("/charge")
    @Operation(summary = "사용자 포인트 충전", description = "사용자의 포인트를 충전합니다.")
    public CustomApiResponse chargePoint(@RequestBody PointRequest request){
        try{
            userPointService.charge(request.getUserId(), request.getPoint());
            return CustomApiResponse.success(ApiMessage.CHARGE_SUCCESS);
        } catch (RuntimeException e) {
            return CustomApiResponse.badRequest(ApiMessage.INVALID_USER);
        }
    }

    @PostMapping("/use")
    @Operation(summary = "사용자 포인트 사용", description = "사용자의 포인트를 사용합니다.")
    public CustomApiResponse usePoint(@RequestBody PointRequest request){
        try{
            userPointService.use(request.getUserId(), request.getPoint());
            return CustomApiResponse.success(ApiMessage.USE_SUCCESS);
        } catch (RuntimeException e) {
            return CustomApiResponse.badRequest(ApiMessage.INVALID_USER);
        }
    }
}
