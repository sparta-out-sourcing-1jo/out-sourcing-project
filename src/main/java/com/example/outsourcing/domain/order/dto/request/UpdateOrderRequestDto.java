package com.example.outsourcing.domain.order.dto.request;

import com.example.outsourcing.common.enums.CancelReason;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateOrderRequestDto {

    @NotNull(message = "Null 일 수 없습니다.")
    private final Boolean isProceed;

    private final CancelReason reason;
}
