package com.romander.bookingapp.mapper;

import com.romander.bookingapp.config.MapperConfig;
import com.romander.bookingapp.dto.payment.PaymentResponseDto;
import com.romander.bookingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    PaymentResponseDto toDto(Payment payment);
}
