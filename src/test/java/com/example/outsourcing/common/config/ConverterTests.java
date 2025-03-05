package com.example.outsourcing.common.config;

import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTests {

    @Test
    void 주문_상태_컨버터_성공() {
        OrderStateConverter converter = new OrderStateConverter();

        assertEquals(OrderState.CLIENT_ACCEPT, converter.convert("client_accept"));
        assertEquals(OrderState.OWNER_CANCEL, converter.convert("owner_cancel"));
        assertNull(converter.convert("invalid_state"));
        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
    }

    @Test
    void 취소_이유_컨버터_성공() {
        CancelReasonConverter converter = new CancelReasonConverter();

        assertEquals(CancelReason.USER_REQUEST, converter.convert("user_request"));
        assertEquals(CancelReason.OUT_OF_STOCK, converter.convert("out_of_stock"));
        assertNull(converter.convert("invalid_reason"));
        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
    }
}
