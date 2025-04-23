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
        var usuario = document.getElementById("usuario").value.trim();
        var password = document.getElementById("password").value.trim();
        var usuarioError = document.getElementById("usuarioError");
        var passwordError = document.getElementById("passwordError");

        usuarioError.textContent = "";
        passwordError.textContent = "";

        var valido = true;

        if (usuario === "") {
            usuarioError.textContent = "El usuario es obligatorio.";
            valido = false;
        }
        if (password === "") {
            passwordError.textContent = "La contraseña es obligatoria.";
            valido = false;
        }
        if (!valido) {
            event.preventDefault();
        }
    });
});