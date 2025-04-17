package kr.hhplus.be.server.interfaces.api.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PointRequest {
    @Schema(description = "사용자 ID", example = "123")
    private Long userId;

    @Schema(description = "충전할 포인트", example = "5000")
    private Long point;
}
