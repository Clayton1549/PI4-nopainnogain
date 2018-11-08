package com.example.littlewolf_pc.app.model;

public class UsuarioDTO {

        private Long id;
        private String nome;
        private String email;
        private String senha;
        private byte[] foto;
        private HistoriaDTO historia;

        public UsuarioDTO() {
            super();
        }

        public UsuarioDTO(Long id, String nome, byte[] foto) {
            this();
            this.id = id;
            this.nome = nome;
            this.foto = foto;
        }

        public UsuarioDTO(Long id) {
            super();
            this.id = id;
        }


        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }


        public String getNome() {
            return nome;
        }
        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }
        public void setSenha(String senha) {
            this.senha = senha;
        }

        public byte[] getFoto() {
            return foto;
        }
        public void setFoto(byte[] foto) {
            this.foto = foto;
        }


        public HistoriaDTO getHistoria() {
            return historia;
        }

        public void setHistoria(HistoriaDTO historia) {
            this.historia = historia;
        }

}