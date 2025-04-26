/**
 * Validación del formulario de inicio de sesión
 * @description Esta función valida el formulario de inicio de sesión al enviarlo.
 * Verifica que los campos de usuario y contraseña no estén vacíos.
 * Si alguno de los campos está vacío, muestra un mensaje de error correspondiente.
 * Si ambos campos son válidos, el formulario se envía normalmente.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0.0
 * @since 1.0.0
 */
document.addEventListener('DOMContentLoaded', function() {
    var formularioLogin = document.getElementById("formularioLogin");
    formularioLogin.addEventListener("submit", function(event) {
        event.preventDefault();  // Evita recarga automática.

        var usuario = document.getElementById("usuario").value.trim();
        var password = document.getElementById("password").value.trim();
        var usuarioError = document.getElementById("usuarioError");
        var passwordError = document.getElementById("passwordError");

        usuarioError.textContent = "";
        passwordError.textContent = "";

        var camposValidos = true;

        if (usuario === "") {
            usuarioError.textContent = "El nombre de usuario es obligatorio";
            camposValidos = false;
        }
        if (password === "") {
            passwordError.textContent = "La contraseña es obligatoria.";
            camposValidos = false;
        }
        if (!camposValidos) {
            return;  
        }

        fetch(this.action, {
            method: this.method,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams(new FormData(this))
        })
        .then(function(response) {
            var destino = new URL(response.url);
            if (destino.pathname === '/menu') {
                window.location = response.url;  // Login correcto.
            } else {
                usuarioError.textContent = "El usuario o la contraseña no son correctos.";
            }
        })
        .catch(function() {
            passwordError.textContent = "Error de red, inténtalo más tarde.";
        });
    });
});