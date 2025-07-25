package com.romander.bookingapp.mapper;

import com.romander.bookingapp.config.MapperConfig;
import com.romander.bookingapp.dto.payment.PaymentRequestDto;
import com.romander.bookingapp.dto.payment.PaymentResponseDto;
import com.romander.bookingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    Payment toModel(PaymentRequestDto requestDto);

    @Mapping(target = "bookingId", source = "booking.id")
    PaymentResponseDto toDto(Payment payment);
}
