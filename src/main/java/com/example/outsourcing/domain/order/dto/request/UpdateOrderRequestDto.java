package com.example.outsourcing.domain.order.dto.request;

import com.example.outsourcing.common.enums.CancelReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateOrderRequestDto {

    private final Boolean isProceed;

    private final CancelReason reason;
}
