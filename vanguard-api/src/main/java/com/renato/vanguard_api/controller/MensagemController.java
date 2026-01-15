package com.renato.vanguard_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renato.vanguard_api.repository.MensagemRepository;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class MensagemController {
    private final MensagemRepository mensagem;

    public MensagemController(MensagemRepository mensagem) {
        this.mensagem = mensagem;
    }

    @GetMapping("/mensagem")
    public MensagemRepository getMensagem() {
        return mensagem;
    }

}
