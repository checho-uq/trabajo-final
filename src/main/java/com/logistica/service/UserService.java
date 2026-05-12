package com.logistica.service;

import com.logistica.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * SOLID - SRP: Única responsabilidad = gestionar usuarios (CRUD + autenticación).
 */
public class UserService {
    private List<Usuario> usuarios;

    public UserService(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario registrarUsuario(String nombre, String email, String tel, String password) {
        String id = "U" + String.format("%03d", usuarios.size() + 1);
        Usuario u = new Usuario(id, nombre, email, tel, password, false);
        usuarios.add(u);
        return u;
    }

    public Usuario iniciarSesion(String email, String password) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst().orElse(null);
    }

    public void actualizarUsuario(Usuario u) {
        // En un sistema real, se buscaría el usuario por ID y se actualizarían sus campos.
        // Aquí, asumimos que el objeto 'u' ya es una referencia al usuario en la lista o
        // que se sobrescribirá uno existente.
        // Para este ejemplo, solo imprimimos un mensaje.
        System.out.println("Usuario " + u.getIdUsuario() + " actualizado.");
    }

    public void eliminarUsuario(String id) {
        usuarios.removeIf(u -> u.getIdUsuario().equals(id));
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public Optional<Usuario> findUserById(String userId) {
        return usuarios.stream().filter(u -> u.getIdUsuario().equals(userId)).findFirst();
    }
}
