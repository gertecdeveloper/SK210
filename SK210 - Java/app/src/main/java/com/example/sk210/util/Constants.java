package com.example.sk210.util;

public class Constants {
    private String nome;
    private int img;

    public Constants(String nome, int img) {
        this.nome = nome;
        this.img = img;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
