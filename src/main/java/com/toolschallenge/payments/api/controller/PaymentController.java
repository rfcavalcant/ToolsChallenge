package com.toolschallenge.payments.api.controller;

import com.toolschallenge.payments.api.dto.PaymentRequestDto;
import com.toolschallenge.payments.api.dto.PaymentResponseDto;
import com.toolschallenge.payments.api.mapper.PaymentMapper;
import com.toolschallenge.payments.service.PaymentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> create(@Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentMapper.toResponse(paymentService.create(paymentMapper.toDomain(request)));
        String transactionId = response.transaction().id();
        return ResponseEntity.created(URI.create("/pagamentos/" + transactionId)).body(response);
    }

    @PostMapping("/{id}/estorno")
    public ResponseEntity<PaymentResponseDto> refund(@PathVariable String id) {
        return ResponseEntity.ok(paymentMapper.toResponse(paymentService.refund(id)));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAll() {
        List<PaymentResponseDto> response = paymentService.getAll().stream()
                .map(paymentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(paymentMapper.toResponse(paymentService.getById(id)));
    }
}
