package com.desafio.agenda_telefonica.dto;

public class ResponseDTO<T> {

    private String status;
    private String mensagem;
    private T data;

    public ResponseDTO(String status, String mensagem, T data) {
        this.status = status;
        this.mensagem = mensagem;
        this.data = data;
    }

    // Getters e Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
